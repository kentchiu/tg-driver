package com.kent.tg.daemon;

import com.kent.tg.client.Telegram;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DebugListener {

    private final Logger logger = LoggerFactory.getLogger(DebugListener.class);
    @Autowired
    private Telegram telegram;

    @EventListener
    public void onAny(TdApi.Object x) {
        if (logger.isTraceEnabled()) {  // use level guard to improve performance
            logger.trace("----> {}", x);
        }
    }

    @EventListener
    public void onUpdateMessageContent(TdApi.UpdateMessageContent content) {
        logger.error("-------------->TdApi.UpdateMessageContent: {}", content);
    }

}