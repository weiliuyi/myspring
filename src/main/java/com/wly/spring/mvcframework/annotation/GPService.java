package com.wly.spring.mvcframework.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented

public @interface GPService {
    String value() default "";
}
