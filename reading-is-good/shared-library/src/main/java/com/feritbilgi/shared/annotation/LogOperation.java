package com.feritbilgi.shared.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {
    String operation() default "";
    String description() default "";
    boolean sendSms() default false;
    String smsTemplate() default "";
}
