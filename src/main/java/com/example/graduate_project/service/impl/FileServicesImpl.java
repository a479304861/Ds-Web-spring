package com.example.graduate_project.service.impl;

import com.example.graduate_project.dao.NamosunUserDao;
import com.example.graduate_project.dao.enity.NamoSunUser;
import com.example.graduate_project.dao.enity.ResponseResult;
import com.example.graduate_project.dao.enity.Result;
import com.example.graduate_project.utiles.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


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

            oneByFileId.setCountNum(countNum);
            oneByFileId.setAnimalName(animalName);
            oneByFileId.setComplete("2");
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
            return false ;
        }
        if (Integer.parseInt(dustLengthThreshold) > 10 || Integer.parseInt(dustLengthThreshold) < 5) {
            return false;
        }
        return true;
    }

    private List<String> fileToList(File file) throws IOException {
        String result = readFile(file);
        assert result != null;
        return TextUtils.toList(result);
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


    public ResponseResult submit(String FileId, String cycleLengthThreshold, String dustLengthThreshold, String countNum, String animalName) {
        if (!checkResult(FileId, cycleLengthThreshold, dustLengthThreshold)) {
            return ResponseResult.FAILED("输入不正确");
        }
        NamoSunUser oneByFileId = fileDao.findOneById(FileId);
        oneByFileId.setComplete("1");
        fileDao.save(oneByFileId);
        return ResponseResult.SUCCESS("提交成功");
    }

    private static void delFile(File f){
        if(!f.exists()){return;}//判断是否存在这个目录
        File [] f_Arr=f.listFiles();//获取目录的所有路径表示的文件
        if (f_Arr!=null){
            for(File f_val : f_Arr){
                //递归
                if(f_val.isDirectory()){
                    delFile(f_val);//递归调用自己
                }
                f_val.delete();//不是文件夹则删除该文件
            }
        }
        f.delete();//删除该目录
    }
    public ResponseResult delete(String id) {
        if(id==null){
            return ResponseResult.FAILED("");
        }
        fileDao.deleteById(id);
        String targetPath = filePath + File.separator + id + ".sequence";
        String targetOutPath=OUT_PATH+File.separator+id;
        File targetFile = new File(targetPath);
        File file = new File(targetOutPath);
        delFile(targetFile);
        delFile(file);
        return ResponseResult.SUCCESS("删除成功");
    }

    public ResponseResult getResult(String id) {
        if(id==null){
            id = CookieUtils.getCookie(getRequest(), ConstantUtils.NAMO_SUM_RESULT_KEY);
            if(id==null){
                return ResponseResult.FAILED();
            }
        }
        File synteny = new File(OUT_PATH + File.separator + id + File.separator + "synteny.txt");
        File blocks = new File(OUT_PATH + File.separator + id + File.separator + "blocks.txt");

        Result  result = null;
        try {
            List<String>  syntenyLists = fileToList(synteny);
            List<String> blocksList = fileToList(blocks);
            NamoSunUser oneById = fileDao.findOneById(id);
            String animalName = oneById.getAnimalName();
            String countNum = oneById.getCountNum();
            List<List<String>> syntenyListsList=new ArrayList<>();
            List<String> syntenyNum=new ArrayList<>();
            List<List<String>> blocksListList=new ArrayList<>();

            for (String s : blocksList) {
                List<String> strings = new ArrayList<>(Arrays.asList(s.split(" ")));
                blocksListList.add(strings);
            }
            for (String s : syntenyLists) {
                List<String> temp = new ArrayList<>(Arrays.asList(s.split(":")));

                List<String> strings = new ArrayList<>(Arrays.asList(temp.get(1).split(" ")));
                syntenyNum.add(strings.get(0));
                temp.clear();
                for (int i = 1; i < strings.size(); i++) {
                    temp.add(strings.get(i));
                }
                syntenyListsList.add(temp);
            }
            List<String> countNumList = TextUtils.splitList(countNum);
            List<String> animalNameList = TextUtils.splitList(animalName);
            CookieUtils.setUpCookie(getResponse(),ConstantUtils.NAMO_SUM_RESULT_KEY,id);
            result = new Result(syntenyListsList,syntenyNum,blocksListList,animalNameList,countNumList);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED();
        }
        return ResponseResult.SUCCESS().setData(result);
    }
}
