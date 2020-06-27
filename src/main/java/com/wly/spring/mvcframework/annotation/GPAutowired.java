package com.wly.spring.mvcframework.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPAutowired {
    String value() default "";
}
