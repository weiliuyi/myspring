package com.wly.spring.mvcframework.v2;

import com.wly.spring.mvcframework.annotation.GPAutowired;
import com.wly.spring.mvcframework.annotation.GPController;
import com.wly.spring.mvcframework.annotation.GPRequestMapping;
import com.wly.spring.mvcframework.annotation.GPService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class GPDispatcherServlet extends HttpServlet {
    private Properties contextConfig = new Properties();
    private List<String> classNames = new ArrayList<String>();
    private Map<String,Object> ioc = new HashMap<String, Object>();
    private Map<String,Method> handlerMapping = new HashMap<String, Method>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Execution Detail" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1 加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2.扫描相关类
        doScanner(contextConfig.getProperty("ScanPackage"));
        //3 初始化相关容器，并且将他们放入到ioc容器中去
        doInstance();
        //4 完成以来注入
        doAutowire();
        //5 初始化HandlerMapping
        initHandlerMapping();
        System.out.println("GP Spring framework is init !!!!!");
    }

    private void doDispatch (HttpServletRequest req,HttpServletResponse resp)  throws Exception {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        if (!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 Not Found!");
            return;
        }
        Method method = this.handlerMapping.get(url);
        Map<String,String[]> parameterMap = req.getParameterMap();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] paramValues = new Object[parameterTypes.length];
        for (int i = 0 ;i < parameterTypes.length;i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class) {
                paramValues[i] = req;
            } else if (parameterType == HttpServletResponse.class) {
                paramValues[i] = resp;
            } else if (parameterType == String.class) {
                GPRequestMapping requestParam = parameterType.getAnnotation(GPRequestMapping.class);
                if (parameterMap.containsKey(requestParam.value())) {
                    for (Map.Entry<String,String[]> param:parameterMap.entrySet()) {
                        String value = Arrays.toString(param.getValue()).
                                replace("\\[|\\]","").replaceAll("\\s",",");
                        paramValues[i] = value;
                    }
                }
            }
        }
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(ioc.get(beanName),paramValues);
    }

    private void initHandlerMapping() {
        for (Map.Entry<String,Object> entry:ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(GPController.class)){continue;}
            String baseUrl = "";
            if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(GPRequestMapping.class)) {
                    continue;
                }
                GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                String url = "/" + baseUrl + "/" + requestMapping.value().
                        replaceAll("/+","/");
                handlerMapping.put(url,method);
            }
        }

    }


    private void doAutowire() {
        for (Map.Entry<String,Object> entry :ioc.entrySet()) {
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
            for (Field  field:declaredFields) {
                if (!field.isAnnotationPresent(GPAutowired.class)) { continue;}
                GPAutowired autowired = field.getAnnotation(GPAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)) {
                 beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void doInstance() {
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(GPController.class)) {
                    Object instance = clazz.newInstance();
                    String simpleName = toLowerFirstCase(clazz.getSimpleName());
                    ioc.put(simpleName, instance);
                } else if (clazz.isAnnotationPresent(GPService.class)) {
                    GPService gpService = clazz.getAnnotation(GPService.class);
                    String beanName = gpService.value();
                    if ("".equals(beanName)) {
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName,instance);
                    //根据类型自动赋值
                    for(Class<?> i :clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())){
                            throw new Exception("the  " + i.getName() + "is exists !");
                        }
                        //直接把接口的值当作key；
                        ioc.put(i.getName(),instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toLowerFirstCase (String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getResource("/" +
                scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file :classPath.listFiles()) {
            if (file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) { continue;}
                String className = scanPackage + "."  + file.getName().replace(".class","");
                classNames.add(className);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            if (is == null){ return;}
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
