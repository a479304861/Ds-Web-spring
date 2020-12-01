package com.example.graduate_project.service;

import com.example.graduate_project.dao.ResponseResult;
import com.example.graduate_project.dao.Result;
import com.example.graduate_project.utiles.ConstantUtils;
import com.example.graduate_project.utiles.RunExeUtils;
import com.example.graduate_project.utiles.SnowflakeIdWorker;
import com.example.graduate_project.utiles.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class FileServicesImpl implements IFileService {

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Value("${Namosun.graduate.file.save-path}")
    public String filePath;

    @Override
    public ResponseResult upload(MultipartFile file) {
        try {
            if (file == null) {
                return ResponseResult.FAILED("上传失败，上传数据为空");
            }
            String id = idWorker.nextId() + "";
            String targetPath = filePath + File.separator + id + ".sequence";
            System.out.println(targetPath);
            File targetFile = new File(targetPath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            try {
                file.transferTo(targetFile);
                return ResponseResult.SUCCESS().setData(id);
            } catch (Exception e) {
                return ResponseResult.FAILED("上传失败,请稍后重试");
            }
        } catch (Exception e) {
            return ResponseResult.FAILED("系统异常，上传失败");
        }
        //500,系统异常
    }


    @Override
    public ResponseResult getResult(String id, String cycleLengthThreshold, String dustLengthThreshold) {
        if (cycleLengthThreshold == null) {
            return ResponseResult.FAILED("参数不可以为空");
        }
        if (id == null) {
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
        param.add(ConstantUtils.PROGRAM_PATH);
        param.add(id);
        param.add(cycleLengthThreshold);
        param.add(dustLengthThreshold);
        Result result;
        try {
            RunExeUtils.openRun(param);
            File synteny = new File(ConstantUtils.OUT_PATH + File.separator + id + File.separator + "synteny.txt");
            File blocks = new File(ConstantUtils.OUT_PATH + File.separator + id + File.separator + "blocks.txt");
            List<String> syntenyLists = fileToList(synteny);
            List<String> blocksList = fileToList(blocks);
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
        try{
            InputStream is = new FileInputStream(file);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            is.close();
            return new String(bytes);
        }catch(Exception e){
            e.printStackTrace();
        }
      return null;
    }




}
