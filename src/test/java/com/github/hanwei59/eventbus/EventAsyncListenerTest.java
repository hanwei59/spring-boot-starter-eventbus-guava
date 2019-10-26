package com.github.hanwei59.eventbus;

import com.github.hanwei59.eventbus.annotation.EventBusListener;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EventBusListener
public class EventAsyncListenerTest {//implements AsyncListener
    @Subscribe
    public void onEvent(TestEvent event) throws Exception {
        log.info("onEvent AsyncListener:" + event);
        if("RuntimeException".equals(event.getText())){
            throw new RuntimeException("RuntimeException");
        }
        if("Exception".equals(event.getText())){
            throw new Exception("Exception");
        }
        if("Error".equals(event.getText())){
            throw new Error("Error");
        }
    }

    @Subscribe
    public void onEvent2(TestEvent event) throws Exception {
        TimeUnit.SECONDS.sleep(2);
        log.info("onEvent2 AsyncListener:" + event);
    }
}
