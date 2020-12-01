package com.example.graduate_project.controller;


import com.example.graduate_project.dao.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ErrorPageController {

    @GetMapping("/404")
    public ResponseResult page404() {
        return ResponseResult.ERROR_404();
    }

    @GetMapping("/403")
    public ResponseResult page403() {
        return ResponseResult.ERROR_403();
    }

    @GetMapping("/405")
    public ResponseResult page405() {
        return ResponseResult.ERROR_405();
    }

    @GetMapping("/504")
    public ResponseResult page504() {
        return ResponseResult.ERROR_504();
    }

    @GetMapping("/505")
    public ResponseResult page505() {
        return ResponseResult.ERROR_505();
    }
}
