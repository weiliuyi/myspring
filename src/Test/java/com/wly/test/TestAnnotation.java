package com.wly.test;

import com.wly.spring.mvcframework.annotation.GPRequestParam;
import org.junit.Test;


import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class TestAnnotation {
    @Test
    public void test1 () throws  ClassNotFoundException {
        Class<?> clazz = Class.forName("com.wly.spring.demo.mvc.DemoAction");
        Method method = clazz.getMethods()[0];
        System.out.println(method);
        //DemoAction demoAction = (DemoAction) clazz.newInstance();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            System.out.println(parameterType);
        }
        Class<?> parameter = parameterTypes[0];
        if (parameter.isAnnotationPresent(GPRequestParam.class)) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
        Annotation[] annotations = parameter.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }

    }

    @Test
    @SuppressWarnings("unchecked")
    public void test2 () throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> clazz = Class.forName("com.wly.test.ParameterAnnotation");
        Method[] methods = clazz.getMethods();
//        for(Method method:methods) {
//            System.out.println(method);
//        }
        Method method = methods[0];
        for (Class<?> aclazz:method.getParameterTypes()) {
            System.out.println(aclazz);
        }
        Class<String> parameterType = (Class<String>) method.getParameterTypes()[0];
        if (parameterType.isAnnotationPresent(GPRequestParam.class)) {
            System.out.println("yes");
        } else {
            System.out.println("no");
        }
        Object invoke = method.invoke(clazz.newInstance(), "hello");
        if (invoke instanceof String) {
            System.out.println("yes");
        }
        System.out.println(invoke.toString() + " out");


    }


    @Test
    public void test3 () throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.wly.spring.demo.mvc.DemoAction");
        Method method = clazz.getMethods()[0];
        System.out.println(method);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (Annotation[] annotations:parameterAnnotations) {
            for (Annotation annotation:annotations) {
                if (annotation instanceof GPRequestParam) {
                    System.out.println(annotation);
                    System.out.println(((GPRequestParam) annotation).value());
                }
            }
        }
    }
}
class ParameterAnnotation {

    @SuppressWarnings("unused")
    public String hello (@GPRequestParam("name") String name) {
        System.out.println("hello " + name + " in");
        return "hello " + name;
    }
    public String hello2 (@Cherry("1111") String param1,@Cherry("2222") String param2) {
        System.out.println("hello2 " + param1 + "  " + param2 + " in");
        return "hello2 " + param1 + "  " + param2 + " in";
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Class<ParameterAnnotation> clazz = ParameterAnnotation.class;
        Method hello2 = clazz.getMethod("hello2", String.class, String.class);

        Annotation[][] parameterAnnotations = hello2.getParameterAnnotations();
        for (Annotation[] annotations:parameterAnnotations) {
            for (Annotation annotation:annotations) {
                if (annotation instanceof Cherry) {
                    System.out.println(((Cherry) annotation).value());
                }
            }
        }

    }
}

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)

@interface Cherry {
    String value() default "";
}


