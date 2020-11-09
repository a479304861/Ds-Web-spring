package com.example.graduate_project.controller;


import org.json.JSONException;
import org.json.JSONObject;
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
    @RequestMapping(value = "/testDemo", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> testDemo(@RequestParam("param1") String  params1){
        Map<String,Object> map1 = new HashMap<String,Object>();
        System.out.println("/testDemo");
        map1.put("data",30001);
        map1.put("username","123");
        return new ResponseEntity<Map<String,Object>>(map1,HttpStatus.OK);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> uploadImageFile(@RequestParam("file") MultipartFile uploadFile,
                                                              @RequestParam("param1")String param1,
                                                              @RequestParam("param2")String param2
                                                              ) throws JSONException {
        Map<String,Object> map1 = new HashMap<String,Object>();
        System.out.println(param1 + param2);
        System.out.println("/uploadImage");
        try {
            if(uploadFile==null){
                map1.put("STATUS", "ERROR");
                map1.put("MSG", "上传失败，上传数据为空");
                return new ResponseEntity<Map<String,Object>>(map1,HttpStatus.OK);
            }

            map1.put("STATUS", "200");
            map1.put("MSG", "上传成功");
            Thread.sleep(2000);
            System.out.println("执行完毕");
            return new ResponseEntity<Map<String,Object>>(map1,HttpStatus.OK);
        } catch (Exception e) {
            map1.put("STATUS", "ERROR");
            map1.put("MSG", "系统异常，上传失败");
            return new ResponseEntity<Map<String,Object>>(map1,HttpStatus.OK);
        }
        //500,系统异常
    }


}
