package com.iwill.alibaba.ttl.controller;

import com.iwill.alibaba.ttl.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private TestService testService ;

    @GetMapping("test1")
    public String testInSyncThread(){
        testService.testInSyncThread();
        return "success";
    }

    @GetMapping("test2")
    public String testInAsyncThread(){
        testService.testInAsyncThread();
        return "success";
    }
}
