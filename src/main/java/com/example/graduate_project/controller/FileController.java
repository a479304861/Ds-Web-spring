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
        return fileService.uploadSequence(uploadFile);
    }

    @PostMapping(value = "/uploadOrthogroups")
    public ResponseResult uploadOrthogroups(@RequestParam("file") MultipartFile uploadFile) {
        return fileService.uploadOrthogroups(uploadFile);
    }

    @PostMapping(value = "/uploadGFF")
    public ResponseResult uploadGFF(@RequestParam("file") MultipartFile uploadFile, @RequestParam("fileId") String fileId) {
        return fileService.uploadGFF(uploadFile, fileId);
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

    @GetMapping(value = "/submitGFF")
    public ResponseResult submitGFF(@RequestParam(value = "id") String id,
                                    @RequestParam(value = "countNum") String countNum,
                                    @RequestParam(value = "animalName") String animalName,
                                    @RequestParam(value = "size", required = false) Integer size) {
        return fileService.submitGFF(id, countNum, animalName, size);
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
    public void download(@RequestParam(value = "id", required = false) String fileId) {
        fileService.downloadFile(fileId);
    }

    @GetMapping("/downloadProgram")
    public void downloadProgram() {
        fileService.downloadProgram();
    }

    @DeleteMapping("/deleteAll")
    public ResponseResult deleteAll() {
        return fileService.deleteAll();
    }


}
