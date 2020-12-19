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

import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.*;


@Service
@Transactional
@Primary
public class FileServicesImpl extends BaseService {


    @Autowired
    private SnowflakeIdWorker idWorker;

    @Value("${Namosun.graduate.file.save-path}")
    public String INPUT_PATH;

    @Value("${Namosun.graduate.program-path}")
    public String PROGRAM_PATH;

    @Value("${Namosun.graduate.out-path}")
    public String OUT_PATH;

    @Value("${Namosun.graduate.isLinux}")
    public boolean isLinux;

    @Autowired
    private NamosunUserDao fileDao;
    //简化点的个数
    private static final int pointNum=1000;

    /**
     * 上传
     *
     * @param file
     * @return
     */
    public ResponseResult upload(MultipartFile file) {
        if (file == null) {
            return ResponseResult.FAILED("上传失败，上传数据为空");
        }
        String fileId = idWorker.nextId() + "";
        try {
            String targetPath = INPUT_PATH + File.separator + fileId + ".sequence";
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
     *
     * @param fileId
     * @param cycleLengthThreshold
     * @param dustLengthThreshold
     * @param countNum
     * @param animalName
     * @return
     */
    public ResponseResult calculate(String fileId, String cycleLengthThreshold, String dustLengthThreshold, String countNum, String animalName) {
        if (!checkParams(fileId, cycleLengthThreshold, dustLengthThreshold)) {
            return ResponseResult.FAILED("输入不正确");
        }

        NamoSunUser oneByFileId = fileDao.findOneById(fileId);
        if (oneByFileId == null) {
            return ResponseResult.FAILED("请稍后重试");
        }

        try {
            //执行C#
            List<String> param = new ArrayList<>();
            if (isLinux) {
                param.add("mono");
            }
            param.add(PROGRAM_PATH);
            param.add(fileId);
            param.add(cycleLengthThreshold);
            param.add(dustLengthThreshold);
            RunExeUtils.openRun(param);
        } catch (Exception e) {
            return ResponseResult.FAILED("失败");
        }
        //储存到数据库
        oneByFileId.setComplete("2");
        oneByFileId.setAnimalName(animalName);
        oneByFileId.setCountNum(countNum);
        oneByFileId.setCycleLengthThreshold(cycleLengthThreshold);
        oneByFileId.setDustLengthThreshold(dustLengthThreshold);
        fileDao.save(oneByFileId);


        //储存分开的文件
        try {
            //读取block
            String blockString = readFile(new File(OUT_PATH + File.separator + fileId + File.separator + "blocks.txt"));
            String modifiedString = readFile(new File(OUT_PATH + File.separator + fileId + File.separator + "modifiedSequence.txt"));
            List<String> countNumSplitCross = TextUtils.splitCross(countNum);
            List<String> animalNameSplitCross = TextUtils.splitCross(animalName);
            //写入
            writeToFileByAnimal(blockString, fileId, countNumSplitCross, animalNameSplitCross, "block");
            writeToFileByAnimal(modifiedString, fileId, countNumSplitCross, animalNameSplitCross, "modifiedSequence");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseResult.SUCCESS();
    }

    public void writeToFileByAnimal(String writeString, String fileId, List<String> countNumSplitCross, List<String> animalNameSplitCross, String last) {
        assert writeString != null;
        List<String> blockList = TextUtils.splitEnter(writeString);

        int nowIndex = 0;
        //获得listByAnimal
        List<List<String>> ListByAnimal = new ArrayList<>();
        for (String numSplitCross : countNumSplitCross) {
            List<String> temp = new ArrayList<>();
            for (int j = nowIndex; j < nowIndex + Integer.parseInt(numSplitCross); j++) {
                temp.add(blockList.get(j));
            }
            ListByAnimal.add(temp);
            nowIndex += Integer.parseInt(numSplitCross);
        }
        //按照物种生成文件
        for (int i = 0; i < animalNameSplitCross.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String list : ListByAnimal.get(i)) {
                stringBuilder.append(list);
                if (ListByAnimal.get(i).indexOf(list) != ListByAnimal.get(i).size() - 1) {
                    stringBuilder.append("\r\n");
                }
            }
            try {
                stringWriteToFile(OUT_PATH + File.separator + fileId + File.separator + last + "_" + animalNameSplitCross.get(i) + ".txt",
                        stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private boolean checkParams(String fileId, String cycleLengthThreshold, String dustLengthThreshold) {
        if (cycleLengthThreshold == null) {
            return false;
        }
        if (fileId == null) {
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
     *
     * @param fileId
     * @param cycleLengthThreshold
     * @param dustLengthThreshold
     * @param countNum
     * @param animalName
     * @return
     */
    public ResponseResult submit(String fileId, String cycleLengthThreshold, String dustLengthThreshold, String countNum, String animalName) {
        if (!checkParams(fileId, cycleLengthThreshold, dustLengthThreshold)) {
            return ResponseResult.FAILED("输入不正确");
        }
        File file = new File(INPUT_PATH + File.separator + fileId + ".sequence");
        if (!file.exists() || fileDao.findOneById(fileId) == null) {
            return ResponseResult.FAILED("请刷新重试");
        }
        try {
            String fileString = readFile(file);
            assert fileString != null;
            List<String> fileList = TextUtils.splitEnter(fileString);
            List<String> countNumList = TextUtils.splitCross(countNum);
            int countNumAll = 0;
            for (String s : countNumList) {
                countNumAll += Integer.parseInt(s);
            }
            if (fileList.size() != countNumAll) {
                return ResponseResult.FAILED("文件基因总条数和输入不匹配");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED();
        }
        NamoSunUser oneByFileId = fileDao.findOneById(fileId);
        if (oneByFileId == null) {
            return ResponseResult.FAILED("请稍后重试");
        }
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
     *
     * @param id
     * @return
     */
    public ResponseResult delete(String id) {
        if (id == null) {
            return ResponseResult.FAILED("");
        }
        if (fileDao.findOneById(id) == null) {
            return ResponseResult.FAILED("请稍微重试");
        }
        fileDao.deleteById(id);
        String targetInputPath = INPUT_PATH + File.separator + id + ".sequence";
        String targetOutPath = OUT_PATH + File.separator + id;
        File inputFile = new File(targetInputPath);
        File outputFile = new File(targetOutPath);
        try {
            delFile(inputFile);
            delFile(outputFile);
        } catch (Exception ignored) {
        }

        return ResponseResult.SUCCESS("删除成功");
    }

    /**
     * 获得结果
     *
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
        if (fileDao.findOneById(id) == null) {
            return ResponseResult.FAILED("数据不存在,请刷新后重试");
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
            return ResponseResult.FAILED("错误，请刷新后重试");
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
     * @param fileId 文件id
     * @return
     */
    public void download(String fileId) {
        if (fileId == null) {
            fileId = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_RESULT_KEY);
            if (fileId == null) {
                return;
            }
        }
        if (fileDao.findOneById(fileId) == null) {
            return;
        }

        //压缩文件
        File zipTarget = new File(OUT_PATH + File.separator + fileId + ".zip");
        FileOutputStream fos1 = null;
        try {
            fos1 = new FileOutputStream(zipTarget);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String targetOutPath = OUT_PATH + File.separator + fileId;
        ZipUtils.toZip(targetOutPath, fos1, true);


//
//        byte[] body = null;
//        InputStream is = null;
//        try {
//            is = new FileInputStream(zipTarget);
//            body = new byte[is.available()];
//            is.read(body);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "attchement;filename=" + zipTarget.getName());
//        HttpStatus statusCode = HttpStatus.OK;
//        HttpServletResponse response = getResponse();
//        response.setContentType("application/zip");
//        response.setCharacterEncoding("utf-8");
//        ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, statusCode);
//        return entity;


        try {
            HttpServletResponse response = getResponse();
            response.setCharacterEncoding("UTF-8");
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(zipTarget.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            OutputStream outStream = null;
            outStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileId.getBytes("UTF-8"), "ISO-8859-1"));
            outStream.write(buffer);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获得比较之后的图
     *
     * @param id
     * @param animalName
     * @return
     */
    public ResponseResult getComp(String id, String animalName) {
        //结果token
        if (id == null) {
            id = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_RESULT_KEY);
            if (id == null) {
                return ResponseResult.FAILED("请刷新后重试");
            }
        }
        //如果id已经被清空
        if (fileDao.findOneById(id) == null) {
            return ResponseResult.FAILED("文件不存在，请刷新重试");
        }
        //动物token
        if (animalName == null) {
            String cookie = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY);
            if (cookie != null) {
                List<String> splitSpace = TextUtils.splitColon(cookie);
                String cookieId = splitSpace.get(0);
                animalName = splitSpace.get(1);
                if (!cookieId.equals(id)) {
                    return ResponseResult.FAILED("请刷新重试");
                }
            } else {
                return ResponseResult.FAILED("请刷新重试");
            }
        }
        //如果传入3个动物
        List<String> splitAnimalList = TextUtils.splitCross(animalName);
        if (splitAnimalList.size() != 2) {
            return ResponseResult.FAILED("错误，请刷新重试");
        }

        String targetOutPath = OUT_PATH + File.separator + id + File.separator + animalName + ".txt";
        String firstOutPath = OUT_PATH + File.separator + id + File.separator + splitAnimalList.get(0) + ".txt";
        String secondOutPath = OUT_PATH + File.separator + id + File.separator + splitAnimalList.get(1) + ".txt";
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
                //读取序列
                File fileFirst = new File(firstOutPath);
                File fileSecond = new File(secondOutPath);
                String fileFirstList = readFile(fileFirst);
                String fileSecondList = readFile(fileSecond);
                assert fileFirstList != null;
                List<String> splitFirstList = TextUtils.splitCross(fileFirstList);
                assert fileSecondList != null;
                List<String> splitSecondList = TextUtils.splitCross(fileSecondList);
                return ResponseResult.SUCCESS().setData(new DetailResult(simpleGraph(graph), splitFirstList, splitSecondList));
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseResult.FAILED("错误，请刷新重试");
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
        try {
            stringWriteToFile(targetOutPath, writeToFile.toString());
            stringWriteToFile(firstOutPath, markListToString(markLineDataFirst));
            stringWriteToFile(secondOutPath, markListToString(markLineDataSecond));

        } catch (IOException e) {
            e.printStackTrace();
        }

        CookieUtils.setUpCookie(getResponse(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY, id + ":" + animalName);
        return ResponseResult.SUCCESS().setData(new DetailResult(simpleGraph(graph), markLineDataFirst, markLineDataSecond));
    }

    public void stringWriteToFile(String targetOutPath, String string) throws IOException {
        FileWriter writer = new FileWriter(targetOutPath);
        writer.write(string);
        writer.flush();
        writer.close();
    }

    private List<List<String>> simpleGraph(List<List<String>> graph) {
        List<List<String>> result = new ArrayList<>();
        if (graph.size() < pointNum) {
            return graph;
        }
        int step = graph.size() / pointNum;
        for (int i = 0; i < graph.size(); i += step) {
            result.add(graph.get(i));
        }
        return result;
    }

    private String markListToString(List<String> markLineData) {
        StringBuilder writeToFile = new StringBuilder();
        for (int i = 0; i < markLineData.size(); i++) {
            writeToFile.append(markLineData.get(i));
            if (i < markLineData.size() - 1) writeToFile.append("-");
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
        int nowCount = 0;
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
                nowCount += syntenyElement.size();
            }
            markLineData.add(String.valueOf(nowCount));
        }
        return result;
    }

    public ResponseResult deleteAll() {
        List<NamoSunUser> allList = fileDao.findAll();
        allList.forEach((list) -> {
            delete(list.getId());
        });
        String targetOutPath = OUT_PATH;
        String targetInputPath = INPUT_PATH;
        delFile(new File(targetOutPath));
        delFile(new File(targetInputPath));
        return ResponseResult.SUCCESS();
    }
}
