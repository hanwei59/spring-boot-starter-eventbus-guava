package com.github.hanwei59.eventbus.exception;

import com.github.hanwei59.eventbus.EventBusUtils;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class EventBusSubscriberExceptionHandler implements SubscriberExceptionHandler {
    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        Method method = context.getSubscriberMethod();

        SubscriberExceptionEvent exceptionEvent = new SubscriberExceptionEvent();
        exceptionEvent.setClassName(context.getSubscriber().getClass().getCanonicalName());
        exceptionEvent.setMethod(method.getName());
        exceptionEvent.setParameterClassName(method.getParameterTypes()[0].getCanonicalName());
        exceptionEvent.setEvent(context.getEvent());
        exceptionEvent.setExceptionMessage(exception.getMessage());
        EventBusUtils.postNoTransaction(exceptionEvent);

        String message = "Exception thrown by subscriber method "
                + method.getName()
                + '('
                + method.getParameterTypes()[0].getName()
                + ')'
                + " on subscriber "
                + context.getSubscriber()
                + " when dispatching event: "
                + context.getEvent();
        log.error(message, exception);
    }
}
