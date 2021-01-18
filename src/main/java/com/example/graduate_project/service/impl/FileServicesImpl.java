package com.example.graduate_project.service.impl;

import com.example.graduate_project.dao.NamosunUserDao;
import com.example.graduate_project.dao.SpecialDao;
import com.example.graduate_project.dao.enity.*;
import com.example.graduate_project.utiles.*;
import com.vladsch.flexmark.util.Pair;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import static com.example.graduate_project.utiles.TextUtils.splitComma;
import static com.example.graduate_project.utiles.TextUtils.splitTab;


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

    @Value("${Namosun.graduate.program-download-path}")
    public String PROGRAM_DOWNLOAD_PATH;

    @Value("${Namosun.graduate.out-path}")
    public String OUT_PATH;

    @Value("${Namosun.graduate.isLinux}")
    public boolean isLinux;

    @Autowired
    private NamosunUserDao fileDao;

    @Autowired
    private SpecialDao specialDao;
    //简化点的个数
    private static final int pointNum = 1000;
    private static final int simpleSize = 40000;

    /**
     * 上传
     *
     * @param file
     * @return
     */
    public ResponseResult uploadSequence(MultipartFile file) {
        if (file == null) {
            return ResponseResult.FAILED("上传失败，上传数据为空");
        }
        System.out.println(file.getContentType());
        String cookie = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_KEY);
        if (cookie == null) {
            return ResponseResult.FAILED("没有登入错误");
        }
        if (TextUtils.isEmpty(file.getName())) {
            return ResponseResult.FAILED("文件名为空错误");
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

        NamoSunUser namoSunFile = new NamoSunUser();
        namoSunFile.setId(fileId);
        namoSunFile.setComplete(ConstantUtils.Complete.SequenceUpload);
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
        oneByFileId.setComplete(ConstantUtils.Complete.SequenceComp);
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
            return ResponseResult.FAILED("失败");
        }
        return ResponseResult.SUCCESS();
    }

    private Map<String, Integer> getGraph12(String fileId, String countNum, List<String> animalNameSplitCross, List<String> syntenyNum) throws IOException {
        List<String> countNumList = getCountNumList(countNum);
        List<List<String>> blocksListList = getBlockList(fileId);       //处理blockList
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < syntenyNum.size(); i++) {       //遍历每个syn
            //TODO:3需要动态获取
            if (Integer.parseInt(syntenyNum.get(i)) >= 3) {
                List<Integer> animalIntList = new ArrayList<>();    //存放物种当前syn所对应的每个个数
                for (int j = 0; j < countNumList.size() - 1; j++) {     //遍历每个物种
                    int tempNum = 0;    //当前物种对应的个数
                    for (int k = Integer.parseInt(countNumList.get(j)); k < Integer.parseInt(countNumList.get(j + 1)); k++) {   //遍历当前物种的所有bocklist
                        List<String> blocksList = blocksListList.get(k);        //0-22
                        for (String blockKey : blocksList) {
                            if (Math.abs(Integer.parseInt(blockKey)) == i) {
                                tempNum++;
                            }
                        }
                    }
                    if (tempNum == 0) {     //如果有个物种的当前syn为0就放弃
                        break;
                    }
                    animalIntList.add(tempNum);
                }
                if (animalIntList.size() != animalNameSplitCross.size()) {  //并非所有物种都有就找下一个
                    continue;
                }
                StringBuilder key = new StringBuilder();
                for (int k = 0; k < animalIntList.size(); k++) {
                    key.append(animalIntList.get(k));
                    if (k != animalIntList.size() - 1) {
                        key.append("-");
                    }
                }
                Integer integer = map.get(key.toString());
                if (integer == null) {
                    integer = 1;
                } else {
                    integer++;
                }
                map.put(key.toString(), integer);
            }
        }
        return map;
    }

    public void writeToFileByAnimal(String writeString,
                                    String fileId,
                                    List<String> countNumSplitCross,
                                    List<String> animalNameSplitCross,
                                    String last) {
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
        if (Integer.parseInt(dustLengthThreshold) > 30 || Integer.parseInt(dustLengthThreshold) < 0) {
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


    public ResponseResult submit(String fileId, String cycleLengthThreshold, String dustLengthThreshold, String countNum, String animalName) {
        if (!checkParams(fileId, cycleLengthThreshold, dustLengthThreshold)) {
            return ResponseResult.FAILED("输入不正确");
        }
        NamoSunUser oneById = fileDao.findOneById(fileId);
        if (oneById == null) {
            return ResponseResult.FAILED("文件不存在");
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
        oneByFileId.setComplete(ConstantUtils.Complete.SequenceSubmit);
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
     * @param fileID
     * @return
     */
    public ResponseResult getResult(String fileID) {
        if (fileID == null) {
            fileID = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_RESULT_KEY);
            if (fileID == null) {
                return ResponseResult.FAILED();
            }
        }
        if (fileDao.findOneById(fileID) == null) {
            return ResponseResult.FAILED("数据不存在,请刷新后重试");
        }
        Result result;
        try {
            List<List<String>> blocksListList = getBlockList(fileID);       //处理blockList
            List<String> syntenyNum = new ArrayList<>();
            getSynList(fileID, syntenyNum);
            NamoSunUser oneById = fileDao.findOneById(fileID);
            String animalName = oneById.getAnimalName();
            String countNum = oneById.getCountNum();
            List<String> splitAnimalByDao = TextUtils.splitCross(animalName);
            String cookieChoseAnimalName = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY);
            List<String> splitChoseAnimalName = new ArrayList<>();
            if (cookieChoseAnimalName != null) {
                List<String> splitSpace = TextUtils.splitColon(cookieChoseAnimalName);
                String choseAnimalNameIndex = splitSpace.get(1);
                String cookieId = splitSpace.get(0);
                if (cookieId.equals(fileID)) {
                    List<String> splitCross = TextUtils.splitCross(choseAnimalNameIndex);
                    splitChoseAnimalName.add(splitAnimalByDao.get(Integer.parseInt(splitCross.get(0))));
                    splitChoseAnimalName.add(splitAnimalByDao.get(Integer.parseInt(splitCross.get(1))));
                }
            }
            List<String> countNumList = getCountNumList(countNum);
            List<String> animalNameList = TextUtils.splitCross(animalName);
            CookieUtils.setUpCookie(getResponse(), ConstantUtils.NAMO_SUM_RESULT_KEY, fileID);
            // 计算图一图三所有结果并返回
            List<Integer> graph11 = getGraph11(blocksListList, countNumList);
            //TODO:处理graph12需要修改
            List<Pair<String, Integer>> graph12 = new ArrayList<>();
            Map<String, Integer> graph12Temp = getGraph12(fileID, countNum, animalNameList, syntenyNum);
            graph12Temp.forEach((key, val) -> {
                graph12.add(Pair.of(key, val));
            });
            graph12.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
            //图三
            Graph3 graph3 = new Graph3();
            for (int i = 0; i < countNumList.size() - 1; i++) {
                Graph3.Graph3Node graph3Node = new Graph3.Graph3Node();

                int tempCategory = 0;
                for (int j = Integer.parseInt(countNumList.get(i)); j < Integer.parseInt(countNumList.get(i + 1)); j++) {
                    for (int k = 0; k < blocksListList.get(j).size(); k++) {
                        Graph3.Graph3Node.Node node = new Graph3.Graph3Node.Node();
                        node.setCategory(tempCategory);
                        node.setValue(blocksListList.get(j).get(k));
                        graph3Node.getNodes().add(node);
                    }
                    tempCategory++;
                }
                graph3.getGraph3().add(graph3Node);
            }
            for (int i = 0; i < graph3.getGraph3().size(); i++) {
                int index = 0;
                for (int j = 0; j < graph3.getGraph3().get(i).getNodes().size(); j++) {
                    for (int k = j + 1; k < graph3.getGraph3().get(i).getNodes().size(); k++) {
                        if (Math.abs(Integer.parseInt(graph3.getGraph3().get(i).getNodes().get(j).getValue()))
                                == Math.abs(Integer.parseInt(graph3.getGraph3().get(i).getNodes().get(k).getValue()))) {
                            Graph3.Graph3Node.Link link = new Graph3.Graph3Node.Link();
                            link.setId(index);
                            link.setSource(j);
                            link.setTarget(k);
                            link.setSourceSyn(graph3.getGraph3().get(i).getNodes().get(j).getCategory());
                            link.setTargetSyn(graph3.getGraph3().get(i).getNodes().get(k).getCategory());
                            graph3.getGraph3().get(i).getLinks().add(link);
                        }
                    }
                }
                if (graph3.getGraph3().get(i).getLinks().size() > 512) {
                    int step = graph3.getGraph3().get(i).getLinks().size() / 512;
                    List<Graph3.Graph3Node.Link> links = new ArrayList<>();
                    for (int i1 = 0; i1 < graph3.getGraph3().get(i).getLinks().size(); i1 += step) {
                        links.add(graph3.getGraph3().get(i).getLinks().get(i1));
                    }
                    graph3.getGraph3().get(i).setLinks(links);
                }
            }
            result = new Result(blocksListList, animalNameList, countNumList, splitChoseAnimalName, graph12, graph11, graph3.getGraph3());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED("错误，请刷新后重试");
        }
        return ResponseResult.SUCCESS().setData(result);
    }

    public List<Integer> getGraph11(List<List<String>> blocksListList, List<String> countNumList) {
        List<Integer> graph11 = new ArrayList<>();
        int curNum = 0;
        for (int i = 0; i < countNumList.size() - 1; i++) {
            {
                int num = 0;
                for (int j = curNum; j < Integer.parseInt(countNumList.get(i + 1)); j++) {
                    num += blocksListList.get(i).size();
                }

                graph11.add(num);
                curNum = Integer.parseInt(countNumList.get(i + 1));
            }
        }
        return graph11;
    }

    private List<List<String>> getSynList(String id, List<String> syntenyNum) throws IOException {
        File synteny = new File(OUT_PATH + File.separator + id + File.separator + "synteny.txt");
        String syntenyLists = readFile(synteny);
        List<String> syntenyList;
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
        List<String> result = new ArrayList<>();
        result.add("0");
        int count = 0;
        for (String s : countNumList) {
            count += Integer.parseInt(s);
            result.add(String.valueOf(count));
        }
        return result;
    }


    /**
     * 下载文件
     *
     * @param fileId 文件id
     * @return
     */
    public void downloadFile(String fileId) {
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


        downloadZip(fileId, zipTarget.getPath());
    }

    public void downloadZip(String zipName, String path) {
        try {
            HttpServletResponse response = getResponse();
            response.setCharacterEncoding("UTF-8");
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            OutputStream outStream;
            outStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(zipName.getBytes("UTF-8"), "ISO-8859-1"));
            outStream.write(buffer);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadProgram() {
        downloadZip("program", PROGRAM_DOWNLOAD_PATH);
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
        NamoSunUser oneById = fileDao.findOneById(id);
        if (oneById == null) {
            return ResponseResult.FAILED("文件不存在，请刷新重试");
        }
        List<String> splitAnimalByDao = TextUtils.splitCross(oneById.getAnimalName());

        //动物token
        if (animalName == null) {
            String cookie = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY);
            if (cookie != null) {
                List<String> splitSpace = TextUtils.splitColon(cookie);
                String cookieId = splitSpace.get(0);
                List<String> animalIndex = TextUtils.splitCross(splitSpace.get(1));
                animalName = splitAnimalByDao.get(Integer.parseInt(animalIndex.get(0))) +
                        '-' + splitAnimalByDao.get(Integer.parseInt(animalIndex.get(1)));
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

                List<String> splitCross = TextUtils.splitCross(animalName);
                CookieUtils.setUpCookie(getResponse(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY, id +
                        ":" + splitAnimalByDao.indexOf(splitCross.get(0)) +
                        "-" + splitAnimalByDao.indexOf(splitCross.get(1)));
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
        CookieUtils.setUpCookie(getResponse(), ConstantUtils.NAMO_SUM_ANIMAL_NAME_KEY, id +
                ":" + splitAnimalByDao.indexOf(splitCross.get(0)) +
                "-" + splitAnimalByDao.indexOf(splitCross.get(1)));
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
                if (Integer.parseInt(blocks.get(i).get(j)) < 0) {
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
        specialDao.deleteAll();
        String targetOutPath = OUT_PATH;
        String targetInputPath = INPUT_PATH;
        delFile(new File(targetOutPath));
        delFile(new File(targetInputPath));
        return ResponseResult.SUCCESS();
    }

    public ResponseResult uploadOrthogroups(MultipartFile file) {
        if (file == null) {
            return ResponseResult.FAILED("上传失败，上传数据为空");
        }
        String cookie = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_KEY);
        if (cookie == null) {
            return ResponseResult.FAILED("没有登入错误");
        }
        if (TextUtils.isEmpty(file.getName())) {
            return ResponseResult.FAILED("文件名为空错误");
        }
        String fileId = idWorker.nextId() + "";
        try {
            String targetPath = INPUT_PATH + File.separator + fileId + File.separator + "Orthogroups.tsv";
            File targetFile = new File(targetPath);
            if (!targetFile.getParentFile().getParentFile().exists()) {
                targetFile.getParentFile().getParentFile().mkdirs();
            }
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

        NamoSunUser namoSunFile = new NamoSunUser();
        namoSunFile.setId(fileId);
        namoSunFile.setComplete(ConstantUtils.Complete.OrthogroupUpload);
        namoSunFile.setCreateTime(new Date());
        namoSunFile.setOriName(file.getOriginalFilename());
        namoSunFile.setUserId(cookie);
        fileDao.save(namoSunFile);
        return ResponseResult.SUCCESS().setData(namoSunFile);
    }

    public ResponseResult uploadGFF(MultipartFile file, String fileId) {
        if (file == null) {
            return ResponseResult.FAILED("上传失败，上传数据为空");
        }
        String cookie = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_KEY);
        if (cookie == null) {
            return ResponseResult.FAILED("没有登入错误");
        }
        if (fileDao.findOneById(fileId) == null) {
            return ResponseResult.FAILED("文件不存在");
        }
        try {
            String targetPath = INPUT_PATH + File.separator + fileId + File.separator + file.getOriginalFilename();
            File targetFile = new File(targetPath);
            try {
                file.transferTo(targetFile);
            } catch (Exception e) {
                return ResponseResult.FAILED("上传失败,请稍后重试");
            }
        } catch (Exception e) {
            return ResponseResult.FAILED("系统异常，上传失败");
        }

        List<Species> allByFileId = specialDao.findAllByFileId(fileId);
        boolean flag = false;
        for (Species speciesItem : allByFileId) {
            if (speciesItem.getSpeciesName().equals(file.getOriginalFilename())) {
                flag = true;
                break;
            }
        }
        //不存在就创建
        Species species = new Species();
        if (!flag) {
            species.setId(idWorker.nextId() + "");
            species.setFileId(fileId);
            species.setSpeciesName(file.getOriginalFilename());
            specialDao.save(species);
        }
        return ResponseResult.SUCCESS().setData(species);
    }

    public ResponseResult getGFFList(String fileId) {
        return ResponseResult.SUCCESS().setData(specialDao.findAllByFileId(fileId));
    }

    public ResponseResult submitGFF(String fileId,
                                    String cycleLengthThreshold,
                                    String dustLengthThreshold,
                                    Integer size) {
        if (size == null || size == 0) size = 4;
        String orthogroups;
        List<String> genomes = new ArrayList<>();
        List<String> splitCrossByAnimalName = new ArrayList<>();
        //        List<String> splitCrossByAnimalName = TextUtils.splitCross(animalName);
        if (fileDao.findOneById(fileId) == null) {
            return ResponseResult.FAILED("File does not exist");
        }
        List<Species> allByFileId = specialDao.findAllByFileId(fileId);
        if(allByFileId.size()==0){
            return ResponseResult.FAILED("The GFF file is not uploaded");
        }
        if(allByFileId.size()==1){
            return ResponseResult.FAILED("At least 2 species");
        }
        StringBuilder animalName = new StringBuilder();
        for (Species species : allByFileId) {
            String speciesItemName = TextUtils.splitPoint(species.getSpeciesName()).get(0);
            animalName.append(speciesItemName);
            if (allByFileId.indexOf(species) != allByFileId.size() - 1) {
                animalName.append("-");
            }
            splitCrossByAnimalName.add(speciesItemName);
        }
        try {
            File OrthogroupsFile = new File(INPUT_PATH + File.separator + fileId + File.separator + "Orthogroups.tsv");
            if (!OrthogroupsFile.exists())
                return ResponseResult.FAILED("TSV file is not uploaded");
            orthogroups = readFile(OrthogroupsFile);
            for (String animalGenome : splitCrossByAnimalName) {
                File genomeFile = new File(INPUT_PATH + File.separator + fileId + File.separator + animalGenome + ".gff");
                if (!genomeFile.exists())
                    return ResponseResult.FAILED("File does not match species");
                genomes.add(readFile(genomeFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED("Failed to open file");
        }
        List<String> splitEnter = TextUtils.splitEnter(orthogroups);
        Map<String, Integer> tempHashMap = new HashMap<>();
        Map<String, Integer> resultHashMap = new HashMap<>();
        int nowIndex = 1;
        int orthoListSize = splitTab(splitEnter.get(0)).size();
        for (int i = 1; i < splitEnter.size(); i++) {
            List<String> splitTab = splitTab(splitEnter.get(i));
            List<String> arrayLists = new ArrayList<>();
            arrayLists.add(splitTab.get(0));          //头组号 OG0000001
            for (int j = 1; j < splitTab.size(); j++) { //判断是否有3个，且每个基因数量不大于size
                if (TextUtils.isEmpty(splitTab.get(j))) {
                    break;
                }
                List<String> splitComma = splitComma(splitTab.get(j));
                if (splitComma.size() > size) {
                    break;
                }
                for (String item : splitComma) {
                    tempHashMap.put(item, nowIndex);
                }
                arrayLists.add(splitTab.get(j));
            }
            if (arrayLists.size() == orthoListSize) {
                resultHashMap.putAll(tempHashMap);
                nowIndex++;
            }
            tempHashMap.clear();
        }
        List<List<String>> sequence = new ArrayList<>();
        StringBuilder countNum = new StringBuilder();
        int allCount = 0;
        for (String genome : genomes) {
            List<List<String>> GFFlist = readGFF(genome, resultHashMap);
            countNum.append(GFFlist.size());
            if (genomes.indexOf(genome) != genomes.size() - 1) {
                countNum.append("-");
            }
            sequence.addAll(GFFlist);
            for (List<String> gffListItem : GFFlist) {
                allCount += gffListItem.size();
            }
        }
        //保存到数据库
        NamoSunUser oneById = fileDao.findOneById(fileId);
        oneById.setAnimalName(animalName.toString());
        oneById.setCountNum(countNum.toString());
        fileDao.save(oneById);
        StringBuilder stringBuilder = new StringBuilder();
        double step = 1;
        if (allCount > simpleSize) {
             step = new BigDecimal((float) simpleSize / allCount).doubleValue();
        }
        Random random = new Random();
        for (List<String> chroms : sequence) {
            for (String chrom : chroms) {
                if (random.nextDouble() < step) {
                    stringBuilder.append(chrom);
                    if (chroms.indexOf(chrom) != chroms.size() - 1) {
                        stringBuilder.append(" ");
                    }
                }
            }
            if (sequence.indexOf(chroms) != sequence.size() - 1) {
                stringBuilder.append("\r\n");
            }
        }
        try {
            stringWriteToFile(INPUT_PATH + File.separator + fileId + ".sequence", stringBuilder.toString());
        } catch (IOException e) {
            return ResponseResult.FAILED("写入失败");
        }
        submit(fileId, cycleLengthThreshold, dustLengthThreshold, oneById.getCountNum(), oneById.getAnimalName());
        return ResponseResult.SUCCESS();
    }

    public static List<List<String>> readGFF(String genome, Map<String, Integer> hashMapList) {
        List<String> splitEnter = TextUtils.splitEnter(genome);
        List<List<String>> result = new ArrayList<>();
        String nowIndex = "";
        List<String> nowList = new ArrayList<>();
        for (int i = 0; i < splitEnter.size(); i++) {
            List<String> splitTab = splitTab(splitEnter.get(i));
            if (i == 0) {
                nowIndex = splitTab.get(0);
            }
            if (!splitTab.get(0).equals(nowIndex)) {
                result.add(nowList);
                nowList = new ArrayList<>();
            }
            if (hashMapList.containsKey(splitTab.get(1))) {
                nowList.add(String.valueOf(hashMapList.get(splitTab.get(1))));
            }
            nowIndex = splitTab.get(0);
        }
        result.add(nowList);

        return result;
    }

    public ResponseResult deleteGFF(String id) {
        Species oneById = specialDao.findOneById(id);
        if (oneById != null) {
            specialDao.deleteById(id);
        }
        return ResponseResult.SUCCESS();
    }

    public ResponseResult getOrthogroups(String fileId) {
        NamoSunUser oneById = fileDao.findOneById(fileId);
        return ResponseResult.SUCCESS().setData(oneById);
    }

    public ResponseResult calculateGFF(String id, String cycleLengthThreshold, String dustLengthThreshold) {
        NamoSunUser oneById = fileDao.findOneById(id);
        return calculate(id, cycleLengthThreshold, dustLengthThreshold, oneById.getCountNum(), oneById.getAnimalName());
    }
}
