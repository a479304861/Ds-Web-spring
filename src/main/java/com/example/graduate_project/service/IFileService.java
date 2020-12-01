package com.example.graduate_project.service;

import com.example.graduate_project.dao.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    ResponseResult upload(MultipartFile file);

    ResponseResult getResult(String id, String cycleLengthThreshold, String dustLengthThreshold);
}
