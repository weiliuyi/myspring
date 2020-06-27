package com.wly.spring.mvcframework.annotation;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface GPRequestParam {
    String value() default "";
}
