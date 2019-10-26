package com.github.hanwei59.eventbus;

import javax.annotation.PostConstruct;

/**
 * 事件监听接口
 * @author hanwei
 */
public interface AsyncListener {

    @PostConstruct
    default void register() {
        EventBusUtils.registerAsync(this);
    }

}
