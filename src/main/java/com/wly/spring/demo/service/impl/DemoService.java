package com.wly.spring.demo.service.impl;

import com.wly.spring.demo.service.IDemoService;
import com.wly.spring.mvcframework.annotation.GPService;

@GPService
public class DemoService implements IDemoService {
    public String get(String name) {
        return "hello  " + name;
    }
}
