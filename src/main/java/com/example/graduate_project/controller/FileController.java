package com.example.graduate_project.controller;

import com.example.graduate_project.dao.ResponseResult;
import com.example.graduate_project.interceptor.CheckTooFrequentCommit;
import com.example.graduate_project.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController("/file")
public class FileController {
    @Autowired
    private IFileService fileService;

    @CheckTooFrequentCommit
    @CrossOrigin(origins = "*", maxAge = 3600)
    @PostMapping(value = "/upload")
    public ResponseResult uploadFile(@RequestParam("file") MultipartFile uploadFile) {
        return fileService.upload(uploadFile);
    }

    @CheckTooFrequentCommit
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping(value = "/getResult")
    public ResponseResult getResult(@RequestParam(value = "id",required = false) String id ,
                                    @RequestParam(value = "cycleLengthThreshold",required = false) String cycleLengthThreshold,
                                    @RequestParam(value = "dustLengthThreshold",required =  false) String dustLengthThreshold) {
        return fileService.getResult(id, cycleLengthThreshold, dustLengthThreshold);
    }


}
