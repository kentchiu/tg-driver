package com.kent.tg.client;

import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class LoggerUtils {

    private static String shrinkText(String text, int maxWidth) {
        return StringUtils.abbreviate(text.replaceAll("\n", " "), maxWidth);
    }

    public static void logMessage(Logger logger, TdApi.Message message) {
        TdApi.MessageContent content = message.content;
        if (content instanceof TdApi.MessageText mt) {
            logger.debug("Text Message: {}", shrinkText(mt.text.toString(), 200));
        } else if (content instanceof TdApi.MessagePhoto mp) {
            logger.info("Photo Message: messageId: {}  uniqueId: {}, caption: {}", message.id, mp.photo.sizes[0].photo.remote.uniqueId, shrinkText(mp.caption.text, 100));
        } else if (content instanceof TdApi.MessageVideo mv) {
            logger.info("Video Message: messageId: {}  uniqueId: {},  fileName: {}, caption: {}", message.id, mv.video.video.remote.uniqueId, mv.video.fileName, shrinkText(mv.caption.text, 100));
        } else if (content instanceof TdApi.MessageDocument md) {
            logger.info("Document Message: {}", shrinkText(md.caption.text, 100));
        } else {
            logger.debug("Other Message: {} - {} : {} ", content.getConstructor(), content.getClass(), shrinkText(content.toString(), 1000));
        }
    }
}
