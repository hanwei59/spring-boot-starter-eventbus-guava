package com.github.hanwei59.eventbus;

import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AsyncEventBusExecutor extends ThreadPoolExecutor {

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    public AsyncEventBusExecutor(int corePoolSize, int maximumPoolSize, int queueSize){
        super(corePoolSize, maximumPoolSize
                ,60
                , TimeUnit.SECONDS
                ,new LinkedBlockingQueue<Runnable>(queueSize)
                ,new ThreadFactoryBuilder().setNameFormat("asyncEventBus-thread-%d").build()
                ,new CallerRunsPolicy()
        );
    }

    public AsyncEventBusExecutor(int corePoolSize, int maximumPoolSize, int queueSize, String threadNamePrefix, boolean rejectedThrowException){
        super(corePoolSize, maximumPoolSize
                ,60
                , TimeUnit.SECONDS
                ,new LinkedBlockingQueue<Runnable>(queueSize)
                ,new ThreadFactoryBuilder().setNameFormat(threadNamePrefix.concat("-thread-%d")).build()
                ,rejectedThrowException ? new AbortPolicy() : new CallerRunsPolicy()
        );
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        startTime.set(System.currentTimeMillis());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        long costMillis = System.currentTimeMillis() - startTime.get();
        startTime.remove();
        int queueSize = this.getQueue().size();
        String methodName = geTargetMethodName(r);
        Object event = getEvent(r);
        if(t != null){
            log.error("异步事件监听{}，执行异常！耗时毫秒：{}，当前线程数：{}（Core:{} Max:{}），剩余任务数：{}（Active:{} Completed:{} All:{}）事件：{}"
                    , methodName
                    , costMillis, this.getPoolSize(), this.getCorePoolSize(), this.getMaximumPoolSize()
                    , queueSize, this.getActiveCount(), this.getCompletedTaskCount(), this.getTaskCount()
                    , JSONUtil.toJsonStr(event), t);
        }else if(this.getPoolSize() == this.getMaximumPoolSize() && queueSize > 1){
            log.error("异步事件监听{}，执行耗时毫秒：{}，当前线程数：{}（Core:{} Max:{}），剩余任务数：{}（Active:{} Completed:{} All:{}）事件：{}"
                    , methodName
                    , costMillis, this.getPoolSize(), this.getCorePoolSize(), this.getMaximumPoolSize()
                    , queueSize, this.getActiveCount(), this.getCompletedTaskCount(), this.getTaskCount()
                    , JSONUtil.toJsonStr(event));
        }else if(costMillis > 1000 || queueSize > 1){
            log.warn("异步事件监听{}，执行耗时毫秒：{}，当前线程数：{}（Core:{} Max:{}），剩余任务数：{}（Active:{} Completed:{} All:{}）事件：{}"
                    , methodName
                    , costMillis, this.getPoolSize(), this.getCorePoolSize(), this.getMaximumPoolSize()
                    , queueSize, this.getActiveCount(), this.getCompletedTaskCount(), this.getTaskCount()
                    , JSONUtil.toJsonStr(event));
        }else if(costMillis > 100) {
            log.info("异步事件监听{}，执行耗时毫秒：{}，当前线程数：{}（Core:{} Max:{}），剩余任务数：{}（Active:{} Completed:{} All:{}）事件：{}"
                    , methodName
                    , costMillis, this.getPoolSize(), this.getCorePoolSize(), this.getMaximumPoolSize()
                    , queueSize, this.getActiveCount(), this.getCompletedTaskCount(), this.getTaskCount()
                    , JSONUtil.toJsonStr(event));
        }else if(log.isDebugEnabled()){
            log.debug("异步事件监听{}，执行耗时毫秒：{}，当前线程数：{}（Core:{} Max:{}），剩余任务数：{}（Active:{} Completed:{} All:{}）事件：{}"
                    , methodName
                    , costMillis, this.getPoolSize(), this.getCorePoolSize(), this.getMaximumPoolSize()
                    , queueSize, this.getActiveCount(), this.getCompletedTaskCount(), this.getTaskCount()
                    , JSONUtil.toJsonStr(event));
        }
    }

    private Object getEvent(Runnable r) {
        Object event = null;
        try {
            Field eventField = r.getClass().getDeclaredFields()[0];
            eventField.setAccessible(true);
            event = eventField.get(r);
        } catch (Exception e) {
            log.error("获取事件报错", e);
        }
        return event;
    }

    private static String geTargetMethodName(Runnable r) {
        String methodName = null;
        try {
            Field field = r.getClass().getDeclaredFields()[1];
            field.setAccessible(true);
            Object o = field.get(r);
            Field methodField = o.getClass().getDeclaredFields().length == 0
                    ? o.getClass().getSuperclass().getDeclaredField("method")
                    : o.getClass().getDeclaredField("method");
            methodField.setAccessible(true);
            Object methodObj = methodField.get(o);
            if(methodObj instanceof Method){
                Method method = (Method) methodObj;
                methodName = method.getDeclaringClass().getSimpleName().concat(".").concat(method.getName());
                //log.debug("调用的方法：{}", methodName);
            }
        } catch (Exception e) {
            log.error("获取方法名报错", e);
        }
        return methodName;
    }
}
