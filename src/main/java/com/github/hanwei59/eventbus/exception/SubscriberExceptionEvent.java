package com.github.hanwei59.eventbus.exception;

import com.github.hanwei59.eventbus.BaseEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriberExceptionEvent extends BaseEvent {

    /**
     * 事件
     */
    private Object event;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法
     */
    private String method;

    /**
     * 参数类名
     */
    private String parameterClassName;

    /**
     * 异常信息
     */
    private String exceptionMessage;

    /**
     * 异常信息堆栈
     */
    private String exceptionStackTrace;
}
