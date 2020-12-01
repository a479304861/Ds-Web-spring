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
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseResult uploadFile(@RequestParam("file") MultipartFile uploadFile) {
        return fileService.upload(uploadFile);
    }

    @CheckTooFrequentCommit
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping(value = "/getResult")
    public ResponseResult getResult(@RequestParam("id") String id,
                                    @RequestParam("cycleLengthThreshold") String cycleLengthThreshold,
                                    @RequestParam("dustLengthThreshold") String dustLengthThreshold) {
        return fileService.getResult(id, cycleLengthThreshold, dustLengthThreshold);
    }


}
