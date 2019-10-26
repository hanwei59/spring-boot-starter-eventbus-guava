package com.github.hanwei59.eventbus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {

    @Bean
    public AnnotationPostProcessor eventBeanPostProcessor() {
        return new AnnotationPostProcessor();
    }
}
