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
    public void query (HttpServletRequest req, HttpServletResponse resq,
                       @GPRequestParam("name") String name){
        String result = demoService.get(name);
        try {
            resq.getWriter().write(result);
        } catch (IOException ignored) {

        }
    }

}
