package com.example.graduate_project.controller;

import com.example.graduate_project.dao.enity.ResponseResult;
import com.example.graduate_project.interceptor.CheckTooFrequentCommit;
import com.example.graduate_project.service.impl.FileServicesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("/file")
public class FileController {

    @Autowired
    private FileServicesImpl fileService;

    @CheckTooFrequentCommit
    @CrossOrigin(origins = "*", maxAge = 3600)
    @PostMapping(value = "/upload")
    public ResponseResult uploadFile(@RequestParam("file") MultipartFile uploadFile) {
        return fileService.upload(uploadFile);
    }

    @CheckTooFrequentCommit
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping(value = "/calculate")
    public ResponseResult calculate(@RequestParam(value = "id") String id,
                                    @RequestParam(value = "cycleLengthThreshold") String cycleLengthThreshold,
                                    @RequestParam(value = "dustLengthThreshold") String dustLengthThreshold,
                                    @RequestParam(value = "countNum") String countNum,
                                    @RequestParam(value = "animalName") String animalName) {
        return fileService.calculate(id, cycleLengthThreshold, dustLengthThreshold, countNum,animalName);
    }

    @GetMapping(value = "/submit")
    public ResponseResult submit(@RequestParam(value = "id") String id,
                                    @RequestParam(value = "cycleLengthThreshold") String cycleLengthThreshold,
                                    @RequestParam(value = "dustLengthThreshold") String dustLengthThreshold,
                                    @RequestParam(value = "countNum") String countNum,
                                    @RequestParam(value = "animalName") String animalName) {
        return fileService.submit(id, cycleLengthThreshold, dustLengthThreshold, countNum,animalName);
    }

    @GetMapping(value = "/delete")
    public ResponseResult delete(@RequestParam(value = "id") String id ) {
        return fileService.delete(id);
    }

    @GetMapping(value = "/getResult")
    public ResponseResult getResult(@RequestParam(value = "id") String id ) {
        return fileService.getResult(id);
    }

}
