package com.github.hanwei59.eventbus;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author hanwei 16/3/11
 */
@Slf4j
@Aspect
@Component
public class AsyncEventBusAspect extends TransactionSynchronizationAdapter {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void registerTransactionSynchronization() {
        TransactionSynchronizationManager.registerSynchronization(this);
        EventBusUtils.inTransaction.set(true);
        log.debug("事务开始：{}", TransactionSynchronizationManager.getCurrentTransactionName());
    }
    /**
     * 事务提交后真正触发事件
     */
    @Override
    public void afterCommit() {
        super.afterCommit();
        log.debug("事务已提交，真正触发异步事件，事务：{}", TransactionSynchronizationManager.getCurrentTransactionName());
        EventBusUtils.doAsyncPost();
    }

    @Override
    public void afterCompletion(int status) {
        super.afterCompletion(status);
        EventBusUtils.inTransaction.remove();
    }
}
