package com.example.graduate_project.controller;

import com.example.graduate_project.dao.enity.ResponseResult;
import com.example.graduate_project.interceptor.CheckTooFrequentCommit;
import com.example.graduate_project.service.impl.FileServicesImpl;
import org.h2.store.fs.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        return fileService.calculate(id, cycleLengthThreshold, dustLengthThreshold, countNum, animalName);
    }

    @GetMapping(value = "/submit")
    public ResponseResult submit(@RequestParam(value = "id") String id,
                                 @RequestParam(value = "cycleLengthThreshold") String cycleLengthThreshold,
                                 @RequestParam(value = "dustLengthThreshold") String dustLengthThreshold,
                                 @RequestParam(value = "countNum") String countNum,
                                 @RequestParam(value = "animalName") String animalName) {
        return fileService.submit(id, cycleLengthThreshold, dustLengthThreshold, countNum, animalName);
    }

    @GetMapping(value = "/delete")
    public ResponseResult delete(@RequestParam(value = "id") String id) {
        return fileService.delete(id);
    }

    @GetMapping(value = "/getResult")
    public ResponseResult getResult(@RequestParam(value = "id", required = false) String id) {
        return fileService.getResult(id);
    }

    @GetMapping(value = "/getComp")
    public ResponseResult getComp(@RequestParam(value = "id", required = false) String id,
                                  @RequestParam(value = "animalName", required = false) String animalName) {
        return fileService.getComp(id, animalName);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam(value = "id", required = false) String id,
                                           @RequestParam(value = "fileName") String fileName) {
        return fileService.download(id, fileName);
    }

    @DeleteMapping("/deleteAll")
    public ResponseResult deleteAll() {
        return fileService.deleteAll();
    }

}
