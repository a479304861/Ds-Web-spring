package com.example.graduate_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/html")
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
