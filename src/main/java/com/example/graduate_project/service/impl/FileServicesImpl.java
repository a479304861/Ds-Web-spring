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

    public ResponseResult getResult(String FileId, String cycleLengthThreshold, String dustLengthThreshold, String countNum) {
        if (cycleLengthThreshold == null) {
            return ResponseResult.FAILED("参数不可以为空");
        }
        if (FileId == null) {
            return ResponseResult.FAILED("参数不可以为空");
        }
        if (dustLengthThreshold == null) {
            return ResponseResult.FAILED("参数不可以为空");
        }
        if (Integer.parseInt(cycleLengthThreshold) > 100 || Integer.parseInt(cycleLengthThreshold) < 10) {
            return ResponseResult.FAILED("参数不正常");
        }
        if (Integer.parseInt(dustLengthThreshold) > 10 || Integer.parseInt(dustLengthThreshold) < 5) {
            return ResponseResult.FAILED("参数不正常");
        }
        List<String> param = new ArrayList<>();

        param.add(PROGRAM_PATH);
        param.add(FileId);
        param.add(cycleLengthThreshold);
        param.add(dustLengthThreshold);
        Result result;
        try {
            RunExeUtils.openRun(param);
            File synteny = new File(OUT_PATH + File.separator + FileId + File.separator + "synteny.txt");
            File blocks = new File(OUT_PATH + File.separator + FileId + File.separator + "blocks.txt");
            List<String> syntenyLists = fileToList(synteny);
            List<String> blocksList = fileToList(blocks);
            NamoSunUser oneByFileId = fileDao.findOneById(FileId);
            oneByFileId.setCountNum(countNum);
            oneByFileId.setComplete("1");
            fileDao.save(oneByFileId);
            result = new Result(syntenyLists, blocksList);
        } catch (Exception e) {
            return ResponseResult.FAILED("失败");
        }
        return ResponseResult.SUCCESS().setData(result);
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


}
