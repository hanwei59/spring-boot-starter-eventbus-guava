package com.github.hanwei59.eventbus;

import com.github.hanwei59.eventbus.exception.EventBusSubscriberExceptionHandler;
import com.google.common.collect.Queues;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事件总线
 * @author hanwei
 */
@Slf4j
@Component
public class EventBusUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 同步事件总线
     */
    private final static EventBus syncEventBus = new EventBus("syncEventBus");

    private static ThreadPoolExecutor asyncEventBusExecutor = new AsyncEventBusExecutor(10, 50, 1000);
    /**
     * 异步事件总线
     */
    private final static AsyncEventBus asyncEventBus = new AsyncEventBus(asyncEventBusExecutor
            , new EventBusSubscriberExceptionHandler());
    
    /**
     * 异步事件队列
     */
    private static final ThreadLocal<Queue<BaseEvent>> queue = ThreadLocal.withInitial(() -> Queues.newArrayDeque());
    
    /**
     * 是否在事务中
     */
    protected static final ThreadLocal<Boolean> inTransaction = ThreadLocal.withInitial(() -> false);

    /**
     * 定时线程池
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Environment environment = applicationContext.getEnvironment();
        Integer corePoolSize = environment.getProperty("eventbus.async.executor.pool.size.core", Integer.class);
        if(corePoolSize != null){
            asyncEventBusExecutor.setCorePoolSize(corePoolSize);
        }
        Integer maxPoolSize = environment.getProperty("eventbus.async.executor.pool.size.max", Integer.class);
        if(corePoolSize != null){
            asyncEventBusExecutor.setMaximumPoolSize(maxPoolSize);
        }
    }

    /**
     * 注册同步监听器
     */
    public static void registerSync(Object listener) {
        syncEventBus.register(listener);
    }
    /**
     * 注册异步监听器
     */
    public static void registerAsync(Object listener) {
        asyncEventBus.register(listener);
    }
    
    /**
     * 提交一个没有事务的事件：先提交同步事件，再提交异步事件
     *
     * @param event 事件对象
     */
    public static void postNoTransaction(BaseEvent event) {
        post(event, false);
    }

    /**
     * 提交一个有事务的事件：先提交同步事件，再提交异步事件
     *
     * @param event 事件对象
     */
    public static void postWhitTransaction(BaseEvent event) {
        post(event, true);
    }
    
    /**
     * 提交一个事件：先提交同步事件，再提交异步事件
     *
     * @param event 事件对象
     */
    public static void post(BaseEvent event, boolean hasTransaction) {
        String eventClassName = event == null ? "null" : event.getClass().getSimpleName();

        try {
            if (log.isInfoEnabled()) {
                log.info("同步事件开始，{}:{}", eventClassName, event.toString());
            }
            syncEventBus.post(event);
            if (log.isInfoEnabled()) {
                log.info("同步事件结束，{}:{}", eventClassName, event.toString());
            }
        } catch (Error e) {
            log.error("同步事件异常，{}:{}", eventClassName, event.toString(), e);
            throw e;
        }

        if(hasTransaction) {
            Queue<BaseEvent> queueForThread = queue.get();
            queueForThread.offer(event);
            if (log.isDebugEnabled()) {
                log.debug((!inTransaction.get() ? "未" : "已") + "在事务中，预存异步事件，{}:{}", eventClassName, event.toString());
            }

            if (!inTransaction.get()) {//未在事务中时，3秒后事仍未发布，则强制发布
                SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
                    if (queueForThread.contains(event)) {
                        asyncEventBus.post(event);
                        queueForThread.remove(event);
                        log.warn("没有事务，定时补发异步事件，{}:{}", event.getClass().getSimpleName(), event.toString());
//                        HealthStatus.getInstance().decrementCount();
                    }
                }, 3, TimeUnit.SECONDS);
            }
        }else{
            asyncEventBus.post(event);
            if(log.isInfoEnabled()) {
                log.info("异步事件已发布，{}:{}", eventClassName, event.toString());
            }
        }

//        HealthStatus.getInstance().incrementCount();
    }
    
    
    /**
     * 触发队列中的异步事件
     */
    protected static void doAsyncPost(){
        Queue<BaseEvent> queueForThread = queue.get();
        if(queueForThread == null || queueForThread.isEmpty()){
            return;
        }
        Object nextEvent = null;
        try {
            while ((nextEvent = queueForThread.poll()) != null) {
                asyncEventBus.post(nextEvent);
                if(log.isInfoEnabled()) {
                    log.info("事务完成，已触发异步事件，{}:{}", nextEvent.getClass().getSimpleName(), nextEvent.toString());
                }
//                HealthStatus.getInstance().decrementCount();
            }
        }catch (Exception e){
            log.error("使用事务，异步事件触发异常，{}:{}", nextEvent != null ? nextEvent.getClass().getSimpleName() : "null", nextEvent.toString(), e);
        }finally {
            queue.remove();
        }
    }

}
