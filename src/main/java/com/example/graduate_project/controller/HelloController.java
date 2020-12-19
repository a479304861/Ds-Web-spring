package com.example.graduate_project.controller;


import com.example.graduate_project.dao.enity.ResponseResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResponseResult get(){
        return ResponseResult.SUCCESS();
    }
}
