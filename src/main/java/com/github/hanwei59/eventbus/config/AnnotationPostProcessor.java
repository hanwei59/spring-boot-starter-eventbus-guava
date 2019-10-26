package com.github.hanwei59.eventbus.config;

import com.github.hanwei59.eventbus.EventBusUtils;
import com.github.hanwei59.eventbus.annotation.EventBusListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class AnnotationPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> ultimateTargetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (ultimateTargetClass.isAnnotationPresent(EventBusListener.class)) {
            EventBusListener listenerAnnotation = bean.getClass().getAnnotation(EventBusListener.class);
            boolean sync = listenerAnnotation.sync();
            log.info("registering an {} event listener {}->{}", sync ? "sync" : "async", beanName, bean);
            if(sync){
                EventBusUtils.registerSync(bean);
            }else {
                EventBusUtils.registerAsync(bean);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
