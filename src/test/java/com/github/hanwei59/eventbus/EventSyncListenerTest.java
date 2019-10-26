package com.github.hanwei59.eventbus;

import com.github.hanwei59.eventbus.annotation.EventBusListener;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EventBusListener(sync = true)
public class EventSyncListenerTest {// implements SyncListener


    @Subscribe
    public void onEvent(TestEvent event) throws Exception {
        log.info("onEvent Listener:" + event);
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
}
