# spring-boot-starter-eventbus-guava
基于Google Guava 事件总线的 spring-boot-starter 组件，对 Guava Event 做了封装增强，支持同步事件、异步事件，让你发布、订阅事件更简单方便。支持 Spring Boot 1.x.x

#特性
- 使用注解订阅事件
- 事件同时发布到同步事件总线和异步事件总线、订阅时决定同步订阅还是异步订阅
- 异步事件在事务结束后才真正发布
- 异步事件总线线程池添加监控日志

# 依赖

    todo

# 使用
## 发布事件

    //在事务方法中发布事件
    EventBusUtils.postWhitTransaction(new TestEvent("test"));
    
    //在非事务方法中发布事件
    EventBusUtils.postNoTransaction(new TestEvent("test"));
    
## 订阅事件
类添加注解：@EventBusListener，方法添加注解：@Subscribe
默认订阅异步事件，如果要订阅同步事件类的注解指定参数sync：@EventBusListener(sync = true)

    @Component
    @EventBusListener
    public class EventAsyncListenerTest {
        @Subscribe
        public void onEvent(TestEvent event) throws Exception {
            log.info("onEvent AsyncListener:" + event);
        }
    }
    
[![Stargazers over time](https://starchart.cc/hanwei59/spring-boot-starter-eventbus-guava.svg)](https://starchart.cc/hanwei59/spring-boot-starter-eventbus-guava)
