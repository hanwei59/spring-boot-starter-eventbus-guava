package com.github.hanwei59.eventbus;

import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
public class EventBusUtilsTest {

    @Autowired
    private EventSyncListenerTest eventSyncListenerTest;

    @Test
    public void postEventRuntimeException(){
        System.out.println(eventSyncListenerTest);
//        ApplicationContext applicationContext = EventBusUtils.getApplicationContext();
//        EventListenerTest eventListenerTest = applicationContext.getBean(EventListenerTest.class);
//        EventBusUtils.registerSync(eventListenerTest);
        EventBusUtils.post(new TestEvent("RuntimeException"), false);
        log.info("after RuntimeException");
    }

    @Test
    public void postEventException(){
        EventBusUtils.post(new TestEvent("Exception"), false);
        log.info("after Exception");
    }

    @Test
    public void postEventError(){
        EventBusUtils.post(new TestEvent("Error"), false);
        log.info("after Error");
    }

    @Test
    public void testTaskTimeout() throws InterruptedException {
        Executors.newSingleThreadScheduledExecutor().schedule(()->{
            TestEvent test2 = new TestEvent("Test2");
            EventBusUtils.post(test2, false);
        }, 2, TimeUnit.SECONDS);
        log.info("after Test");
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void postText(){
        EventBusUtils.post(new TestEvent("text"), false);
        log.info("after text");
    }

    @AfterClass
    public static void afterClass(){
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
