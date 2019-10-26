package com.github.hanwei59.eventbus.annotation;

import com.github.hanwei59.eventbus.config.EventBusConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EventBusConfig.class)
public @interface EventBusListener {

    /**
     * 是否同步监听，默认false
     */
    boolean sync() default false;
}
