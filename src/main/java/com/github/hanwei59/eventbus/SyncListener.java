package com.github.hanwei59.eventbus;

import javax.annotation.PostConstruct;

/**
 * 事件监听接口
 * @author hanwei
 */
public interface SyncListener {

    @PostConstruct
    default void register() {
        EventBusUtils.registerSync(this);
    }
}
