package com.kaii.dentix;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${test}")
    private String test;

    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        System.out.println("test : " + test);
        return "hello " + name;
    }
}
