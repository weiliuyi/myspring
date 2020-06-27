package com.wly.spring.mvcframework.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface GPRequestMapping {
    String value() default "";
}
