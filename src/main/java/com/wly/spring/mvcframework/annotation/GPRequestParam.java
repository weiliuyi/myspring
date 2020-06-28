package com.wly.spring.mvcframework.annotation;


import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER,ElementType.METHOD,ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface GPRequestParam {
    String value() default "";
}
