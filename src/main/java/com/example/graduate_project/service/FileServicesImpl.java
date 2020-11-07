package com.example.graduate_project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServicesImpl implements  IFileService{

    @Override
    public String upload(MultipartFile file) {
        //是否是文件
        if (file==null) {
            return  "";
        }
        String contentType = file.getContentType();

        //判断文件类型，只支持文件
        //获取相关数据
        //根据我们定的规则进行命名
        //记录文件
        //保存文件
        //返回结果
        return null;
    }
}
