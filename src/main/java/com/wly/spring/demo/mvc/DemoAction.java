package com.wly.spring.demo.mvc;


import com.wly.spring.demo.service.IDemoService;
import com.wly.spring.mvcframework.annotation.GPAutowired;
import com.wly.spring.mvcframework.annotation.GPController;
import com.wly.spring.mvcframework.annotation.GPRequestMapping;
import com.wly.spring.mvcframework.annotation.GPRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@GPController
@GPRequestMapping("/demo")
public class DemoAction {

    @GPAutowired
    private IDemoService demoService;

    @GPRequestMapping("/query")
    public void query (@GPRequestParam("name")String name,
                       HttpServletRequest req, HttpServletResponse resq){
        String result = demoService.get(name);
        System.out.println(result);
        try {
            resq.getWriter().write(result);
        } catch (IOException ignored) {

        }
    }

}
