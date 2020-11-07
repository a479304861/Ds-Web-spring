package com.example.graduate_project.controller;


import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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

    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> uploadImageFile(@RequestParam("img") MultipartFile uploadImage) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            if(uploadImage==null){
                json.put("STATUS", "ERROR");
                json.put("MSG", "上传失败，上传数据为空");
                return new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
            }
            String suffix = uploadImage.getContentType().toLowerCase();//图片后缀，用以识别哪种格式数据
            suffix = suffix.substring(suffix.lastIndexOf("/")+1);
            if(suffix.equals("sequence")) {
                json.put("STATUS", "200");
                json.put("MSG", "上传成功");
                return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
            }
            json.put("STATUS", "ERROR");
            json.put("MSG", "上传格式非法");
            return new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);//500,系统异常
        } catch (Exception e) {
            json.put("STATUS", "ERROR");
            json.put("MSG", "系统异常，上传失败");
            return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
        }
    }
}
