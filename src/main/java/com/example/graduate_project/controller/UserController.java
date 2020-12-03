package com.example.graduate_project.controller;

import com.example.graduate_project.dao.enity.ResponseResult;
import com.example.graduate_project.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/checkUser")
    public ResponseResult checkUser(){
        return userService.checkUser();
    }
}
