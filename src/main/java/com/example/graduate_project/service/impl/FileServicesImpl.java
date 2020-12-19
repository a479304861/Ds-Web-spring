package com.example.graduate_project.service.impl;

import com.example.graduate_project.dao.NamosunUserDao;
import com.example.graduate_project.dao.enity.DetailResult;
import com.example.graduate_project.dao.enity.NamoSunUser;
import com.example.graduate_project.dao.enity.ResponseResult;
import com.example.graduate_project.dao.enity.Result;
import com.example.graduate_project.utiles.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.util.*;


@Service
@Transactional
@Primary
public class FileServicesImpl extends BaseService {

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Value("${Namosun.graduate.file.save-path}")
    public String filePath;

    @Value("${Namosun.graduate.program-path}")
    public String PROGRAM_PATH;

    @Value("${Namosun.graduate.out-path}")
    public String OUT_PATH;

    @Autowired
    private NamosunUserDao fileDao;

    /**
     * 上传
     * @param file
     * @return
     */
    public ResponseResult upload(MultipartFile file) {
        if (file == null) {
            return ResponseResult.FAILED("上传失败，上传数据为空");
        }
        String fileId = idWorker.nextId() + "";
        try {
            String targetPath = filePath + File.separator + fileId + ".sequence";
            System.out.println(targetPath);
            File targetFile = new File(targetPath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            try {
                file.transferTo(targetFile);
                //保存文件在数据库
            } catch (Exception e) {
                return ResponseResult.FAILED("上传失败,请稍后重试");
            }
        } catch (Exception e) {
            return ResponseResult.FAILED("系统异常，上传失败");
        }
        String cookie = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_KEY);
        if (cookie == null) {
            return ResponseResult.FAILED("错误");
        }
        if (TextUtils.isEmpty(file.getName())) {
            return ResponseResult.FAILED("错误");
        }
        NamoSunUser namoSunFile = new NamoSunUser();
        namoSunFile.setId(fileId);
        namoSunFile.setComplete("0");
        namoSunFile.setCreateTime(new Date());
        namoSunFile.setOriName(file.getOriginalFilename());
        namoSunFile.setUserId(cookie);
        fileDao.save(namoSunFile);
        return ResponseResult.SUCCESS().setData(fileId);
    }

    /**
     * 计算
     * @param FileId
     * @param cycleLengthThreshold
     * @param dustLengthThreshold
     * @param countNum
     * @param animalName
     * @return
     */
    public ResponseResult calculate(String FileId, String cycleLengthThreshold, String dustLengthThreshold, String countNum, String animalName) {
        if (!checkResult(FileId, cycleLengthThreshold, dustLengthThreshold)) {
            return ResponseResult.FAILED("输入不正确");
        }
        List<String> param = new ArrayList<>();
        NamoSunUser oneByFileId = fileDao.findOneById(FileId);
        param.add(PROGRAM_PATH);
        param.add(FileId);
        param.add(cycleLengthThreshold);
        param.add(dustLengthThreshold);

        try {
            RunExeUtils.openRun(param);
            oneByFileId.setComplete("2");
            oneByFileId.setAnimalName(animalName);
            oneByFileId.setCountNum(countNum);
            oneByFileId.setCycleLengthThreshold(cycleLengthThreshold);
            oneByFileId.setDustLengthThreshold(dustLengthThreshold);
            fileDao.save(oneByFileId);
        } catch (Exception e) {
            return ResponseResult.FAILED("失败");
        }
        return ResponseResult.SUCCESS();
    }


    private boolean checkResult(String FileId, String cycleLengthThreshold, String dustLengthThreshold) {
        if (cycleLengthThreshold == null) {
            return false;
        }
        if (FileId == null) {
            return false;
        }
        if (dustLengthThreshold == null) {
            return false;
        }
        if (Integer.parseInt(cycleLengthThreshold) > 100 || Integer.parseInt(cycleLengthThreshold) < 10) {
            return false;
        }
        if (Integer.parseInt(dustLengthThreshold) > 10 || Integer.parseInt(dustLengthThreshold) < 5) {
            return false;
        }
        return true;
    }


    private String readFile(File file) throws IOException {
        try {
            InputStream is = new FileInputStream(file);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            is.close();
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 提交
     * @param FileId
     * @param cycleLengthThreshold
     * @param dustLengthThreshold
     * @param countNum
     * @param animalName
     * @return
     */
    public ResponseResult submit(String FileId, String cycleLengthThreshold, String dustLengthThreshold, String countNum, String animalName) {
        if (!checkResult(FileId, cycleLengthThreshold, dustLengthThreshold)) {
            return ResponseResult.FAILED("输入不正确");
        }
        File file = new File(filePath + File.separator + FileId + ".sequence");
        try {
            String fileString = readFile(file);
            List<String> fileList = TextUtils.splitEnter(fileString);
            List<String> countNumList = TextUtils.splitCross(countNum);
            int countNumAll=0;
            for (String s : countNumList) {
                countNumAll+=Integer.parseInt(s);
            }
            if(fileList.size()!=countNumAll){
                return ResponseResult.FAILED();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED();
        }
        NamoSunUser oneByFileId = fileDao.findOneById(FileId);
        oneByFileId.setAnimalName(animalName);
        oneByFileId.setCountNum(countNum);
        oneByFileId.setCycleLengthThreshold(cycleLengthThreshold);
        oneByFileId.setDustLengthThreshold(dustLengthThreshold);
        oneByFileId.setComplete("1");
        fileDao.save(oneByFileId);
        return ResponseResult.SUCCESS("提交成功");
    }

    private static void delFile(File file) {
        if (!file.exists()) {
            return;
        }//判断是否存在这个目录
        File[] f_Arr = file.listFiles();//获取目录的所有路径表示的文件
        if (f_Arr != null) {
            for (File f_val : f_Arr) {
                //递归
                if (f_val.isDirectory()) {
                    delFile(f_val);//递归调用自己
                }
                f_val.delete();//不是文件夹则删除该文件
            }
        }
        file.delete();//删除该目录
    }

    /**
     * 删除
     * @param id
     * @return
     */
    public ResponseResult delete(String id) {
        if (id == null) {
            return ResponseResult.FAILED("");
        }
        fileDao.deleteById(id);
        String targetInputPath = filePath + File.separator + id + ".sequence";
        String targetOutPath = OUT_PATH + File.separator + id;
        File inputFile = new File(targetInputPath);
        File outputFile = new File(targetOutPath);
        delFile(inputFile);
        delFile(outputFile);
        return ResponseResult.SUCCESS("删除成功");
    }

    /**
     * 获得结果
     * @param id
     * @return
     */
    public ResponseResult getResult(String id) {
        if (id == null) {
            id = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_RESULT_KEY);
            if (id == null) {
                return ResponseResult.FAILED();
            }
        }
        Result result = null;
        try {
            List<List<String>> blocksListList = getBlockList(id);       //处理blockList
            List<String> syntenyNum = new ArrayList<>();
            List<List<String>> syntenyListsList = getSynList(id, syntenyNum);
            NamoSunUser oneById = fileDao.findOneById(id);
            String animalName = oneById.getAnimalName();
            String countNum = oneById.getCountNum();
            String cookieChoseAnimalName = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY);
            List<String> splitChoseAnimalName = null;
            if (cookieChoseAnimalName != null) {
                List<String> splitSpace = TextUtils.splitColon(cookieChoseAnimalName);
                String choseAnimalName = splitSpace.get(1);
                String cookieId = splitSpace.get(0);
                if (cookieId.equals(id)) {
                    splitChoseAnimalName = TextUtils.splitCross(choseAnimalName);
                }
            }
            List<String> countNumList = getCountNumList(countNum);
            List<String> animalNameList = TextUtils.splitCross(animalName);
            CookieUtils.setUpCookie(getResponse(), ConstantUtils.NAMO_SUM_RESULT_KEY, id);
            result = new Result(syntenyListsList, syntenyNum, blocksListList, animalNameList, countNumList, splitChoseAnimalName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED();
        }
        return ResponseResult.SUCCESS().setData(result);
    }

    private List<List<String>> getSynList(String id, List<String> syntenyNum) throws IOException {
        File synteny = new File(OUT_PATH + File.separator + id + File.separator + "synteny.txt");
        String syntenyLists = readFile(synteny);
        List<String> syntenyList = null;
        if (syntenyLists == null) {
            return null;
        }
        syntenyList = TextUtils.splitEnter(syntenyLists);

        List<List<String>> syntenyListsList = new ArrayList<>();
        for (String s : syntenyList) {
            List<String> temp = new ArrayList<>(Arrays.asList(s.split(":")));
            List<String> strings = new ArrayList<>(Arrays.asList(temp.get(1).split(" ")));
            syntenyNum.add(strings.get(0));
            temp.clear();
            for (int i = 1; i < strings.size(); i++) {
                temp.add(strings.get(i));
            }
            syntenyListsList.add(temp);
        }
        return syntenyListsList;
    }

    private List<List<String>> getBlockList(String id) throws IOException {
        File blocks = new File(OUT_PATH + File.separator + id + File.separator + "blocks.txt");
        String blocksList = readFile(blocks);
        if (blocksList == null) {
            return null;
        }
        List<String> blocksLists = TextUtils.splitEnter(blocksList);
        List<List<String>> blocksListList = new ArrayList<>();
        for (String s : blocksLists) {
            List<String> strings = new ArrayList<>(Arrays.asList(s.split(" ")));
            blocksListList.add(strings);
        }
        return blocksListList;
    }

    private List<String> getCountNumList(String countNum) {
        List<String> countNumList = TextUtils.splitCross(countNum);

        for (int i = 1; i < countNumList.size(); i++) {
            countNumList.set(i, String.valueOf(Integer.parseInt(countNumList.get(i - 1)) + Integer.parseInt(countNumList.get(i))));
        }
        countNumList.add("0");
        countNumList.sort(Comparator.naturalOrder());
        return countNumList;
    }


    /**
     * 下载文件
     *
     * @param id       文件id
     * @param fileName 是哪个文件
     * @return
     */
    public ResponseEntity<byte[]> download(String id, String fileName) {
        if (id == null) {
            id = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_RESULT_KEY);
            if (id == null) {
                return null;
            }
        }
        String targetOutPath = OUT_PATH + File.separator + id + File.separator + fileName + ".txt";
        File file = new File(targetOutPath);
        byte[] body = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            body = new byte[is.available()];
            is.read(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + file.getName());
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, statusCode);
        return entity;
    }

    /**
     * 获得比较之后的图2
     * @param id
     * @param animalName
     * @return
     */
    public ResponseResult getComp(String id, String animalName) {
        //结果token
        if (id == null) {
            id = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_RESULT_KEY);
            if (id == null) {
                return ResponseResult.FAILED();
            }
        }
        //动物token

        if (animalName == null) {
            String cookie = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY);
            if (cookie != null) {
                List<String> splitSpace = TextUtils.splitColon(cookie);
                String cookieId = splitSpace.get(0);
                animalName = splitSpace.get(1);
                if (!cookieId.equals(id)) {
                    return ResponseResult.FAILED();
                }
            } else {
                return ResponseResult.FAILED();
            }
        }
        //如果传入3个动物
        List<String> splitAnimalList = TextUtils.splitCross(animalName);
        if(splitAnimalList.size()!=2){
            return ResponseResult.FAILED("失败");
        }

        String targetOutPath = OUT_PATH + File.separator + id + File.separator + animalName + ".txt";
        String firstOutPath=OUT_PATH + File.separator + id + File.separator + splitAnimalList.get(0) + ".txt";
        String secondOutPath=OUT_PATH + File.separator + id + File.separator + splitAnimalList.get(1) + ".txt";
        File file = new File(targetOutPath);
        //如果存在就取出来
        if (file.exists()) {
            try {

                String fileList = readFile(file);
                assert fileList != null;
                List<String> splitSpace = TextUtils.splitSpace(fileList);
                List<List<String>> graph = new ArrayList<>();
                for (String item : splitSpace) {
                    List<String> splitCross = TextUtils.splitCross(item);
                    graph.add(splitCross);
                }
                CookieUtils.setUpCookie(getResponse(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY, id + ":" + animalName);
                File fileFirst = new File(firstOutPath);
                File fileSecond = new File(secondOutPath);
                String fileFirstList = readFile(fileFirst);
                String fileSecondList = readFile(fileSecond);
                assert fileFirstList != null;
                List<String> splitFirstList = TextUtils.splitCross(fileFirstList);
                assert fileSecondList != null;
                List<String> splitSecondList = TextUtils.splitCross(fileSecondList);
                return ResponseResult.SUCCESS().setData(new DetailResult(simpleGraph(graph),splitFirstList,splitSecondList));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //不存在就创建
        NamoSunUser oneById = fileDao.findOneById(id);
        List<String> splitCross = TextUtils.splitCross(animalName);
        String animalNameByDao = oneById.getAnimalName();
        List<String> animalNameByDaoSplit = TextUtils.splitCross(animalNameByDao);
        List<String> firstList;
        List<String> secondList;
        List<String> markLineDataFirst;
        List<String> markLineDataSecond;
        //获取syn长序列
        try {
            markLineDataFirst = new ArrayList<>();
            firstList = getSynLongList(0,
                    splitCross,
                    animalNameByDaoSplit,
                    getCountNumList(oneById.getCountNum()),
                    getBlockList(id),
                    getSynList(id, new ArrayList<>()),
                    markLineDataFirst);
            if (firstList == null) {
                return ResponseResult.FAILED("输入不正确");
            }
            markLineDataSecond = new ArrayList<>();
            secondList = getSynLongList(1,
                    splitCross,
                    animalNameByDaoSplit,
                    getCountNumList(oneById.getCountNum()),
                    getBlockList(id),
                    getSynList(id, new ArrayList<>()),
                    markLineDataSecond);
            if (secondList == null) {
                return ResponseResult.FAILED("输入不正确");
            }
        } catch (Exception e) {
            return ResponseResult.FAILED("打开文件失败");
        }
        //根据长序列获得相等的节点
        List<List<String>> graph = new ArrayList<>();
        for (int i = 0; i < firstList.size(); i++) {
            for (int j = 0; j < secondList.size(); j++) {
                if (firstList.get(i).equals(secondList.get(j))) {
                    List<String> temp = new ArrayList<>();
                    temp.add(String.valueOf(i));
                    temp.add(String.valueOf(j));
                    temp.add(firstList.get(i));
                    graph.add(temp);
                }
            }
        }
        //写入文件
        StringBuilder writeToFile = new StringBuilder();
        for (List<String> strings : graph) {
            for (int j = 0; j < strings.size(); j++) {
                if (j != strings.size() - 1) {
                    writeToFile.append(strings.get(j)).append("-");
                } else {
                    writeToFile.append(strings.get(j));
                }
            }
            writeToFile.append(" ");
        }



        FileWriter writer;
        FileWriter writer1;
        FileWriter writer2;
        try {
            writer = new FileWriter(targetOutPath);
            writer.write(writeToFile.toString());

            writer1 = new FileWriter(firstOutPath);
            writer1.write(markListToString(markLineDataFirst));

            writer2 = new FileWriter(secondOutPath);
            writer2.write(markListToString(markLineDataSecond));
            writer.flush();
            writer.close();
            writer1.flush();
            writer1.close();
            writer2.flush();
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CookieUtils.setUpCookie(getResponse(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY, id + ":" + animalName);
        return ResponseResult.SUCCESS().setData(new DetailResult(simpleGraph(graph),markLineDataFirst,markLineDataSecond));
    }

    private List<List<String>> simpleGraph(List<List<String>> graph) {
        List<List<String>> result=  new ArrayList<>();
        if(graph.size()<2000){
            return  graph;
        }
        int step=graph.size()/2000;
        for (int i = 0; i < graph.size(); i+=step) {
            result.add(graph.get(i));
        }
        return result;
    }

    private String markListToString(List<String> markLineData) {
        StringBuilder writeToFile = new StringBuilder();
        for (int i = 0; i < markLineData.size(); i++) {
             writeToFile.append(markLineData.get(i));
            if(i<markLineData.size()-1) writeToFile.append("-");
        }
        return writeToFile.toString();
    }

    /**
     * 根据countNum，synteny，blocks获得图二的一条链
     *
     * @param countNum
     * @param blocks
     * @param synteny
     * @param
     * @return
     */
    private List<String> getSynLongList(int num,
                                        List<String> splitCross,
                                        List<String> animalNameByDaoSplit,
                                        List<String> countNum,
                                        List<List<String>> blocks,
                                        List<List<String>> synteny, List<String> markLineData) {
        List<String> result = new ArrayList<>();
        int index = animalNameByDaoSplit.indexOf(splitCross.get(num));
        if (index == -1) return null;
        int nowCount=0;
        for (int i = Integer.parseInt(countNum.get(index)); i < Integer.parseInt(countNum.get(index + 1)); i++) {
            for (int j = 0; j < blocks.get(i).size(); j++) {
                List<String> syntenyElement = synteny.get(Math.abs(Integer.parseInt(blocks.get(i).get(j))));
                if (Integer.parseInt(blocks.get(i).get(j)) > 0) {
                    result.addAll(syntenyElement);
                } else {
                    for (int k = syntenyElement.size() - 1; k >= 0; k--) {
                        result.add(syntenyElement.get(k));
                    }
                }
                nowCount+=syntenyElement.size();
            }
            markLineData.add(String.valueOf(nowCount));
        }
        return result;
    }
}
