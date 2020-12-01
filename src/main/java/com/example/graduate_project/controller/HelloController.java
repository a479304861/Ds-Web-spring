package com.example.graduate_project.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@RestController
public class HelloController {


    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> get(){
        Map<String,Object> map1 = new HashMap<String,Object>();
        System.out.println("/hello");
        map1.put("data",30001);
        map1.put("username","123");
        return new ResponseEntity<Map<String,Object>>(map1,HttpStatus.OK);
    }




}
