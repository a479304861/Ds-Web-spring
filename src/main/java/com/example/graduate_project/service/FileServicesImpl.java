package com.example.graduate_project.service;

import com.example.graduate_project.dao.ResponseResult;
import com.example.graduate_project.utiles.ConstantUtils;
import com.example.graduate_project.utiles.RunExeUtils;
import com.example.graduate_project.utiles.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FileServicesImpl implements  IFileService{

    @Autowired
    private SnowflakeIdWorker idWorker;

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

    @Value("${Namosun.graduate.file.save-path}")
    public String filePath;

    @Override
    public ResponseResult upload(MultipartFile file) {
        try {
            if(file==null){
                return  ResponseResult.FAILED("上传失败，上传数据为空");
            }
            String id = idWorker.nextId() + "";
            String targetPath = filePath+  File.separator + id + ".sequence" ;
            System.out.println(targetPath);
            File targetFile = new File(targetPath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            try{
                file.transferTo(targetFile);
                return  ResponseResult.SUCCESS().setData(id);
            }catch (Exception e){
                return ResponseResult.FAILED("上传失败,请稍后重试");
            }
        } catch (Exception e) {
            return  ResponseResult.FAILED("系统异常，上传失败");
        }
        //500,系统异常
    }



    @Override
    public ResponseResult getResult(String id, String cycleLengthThreshold, String dustLengthThreshold) {

        List<String> param = new ArrayList<>();


        param.add( ConstantUtils.PROGRAM_PATH);
        param.add(filePath+  File.separator  +id+".sequence");
        param.add(cycleLengthThreshold);
        param.add(dustLengthThreshold);
        try{
            RunExeUtils.openRun(param);
        }catch (Exception e){
            return ResponseResult.FAILED("失败");
        }
        return ResponseResult.SUCCESS();
    }



}
