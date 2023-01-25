package com.kent.tg.daemon;

import com.kent.tg.client.Telegram;
import it.tdlight.client.TelegramError;
import it.tdlight.jni.TdApi;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 認證階段
 */
enum Stage {
    /**
     * 需要手機號
     */
    PHONE_NUMBER,
    /**
     * 已經確定手機號正確， 需要認證碼
     */
    AUTH_CODE,
    /**
     * 已經通過認證
     */
    AUTHENTICATED
}

@Component
public class AuthListener {

    private final Logger logger = LoggerFactory.getLogger(AuthListener.class);

    @Autowired
    private Telegram telegram;
    private Optional<TelegramError> telegramError = Optional.empty();

    private Stage stage = Stage.PHONE_NUMBER;
    private TdApi.AuthorizationState authorizationState;

    public Stage getStage() {
        return this.stage;
    }

    @NotNull
    public Map<String, Object> basicInfo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("stage", stage);
        if (telegramError.isPresent()) {
            result.put("code", telegramError.get().getErrorCode());
            result.put("message", telegramError.get().getMessage());
        }
        if (authorizationState instanceof TdApi.AuthorizationStateWaitCode state) {
            result.put("phoneNumber", state.codeInfo.phoneNumber);
        }
        return result;
    }

    @EventListener
    public void onAuthError(TelegramError error) {
        logger.error("auth error", error);
        this.telegramError = Optional.of(error);
    }

    @EventListener
    public void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        this.authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateWaitPhoneNumber) {
            stage = Stage.PHONE_NUMBER;
            logger.info("[AUTH] AuthorizationStateWaitPhoneNumber: {}", update);
        } else if (authorizationState instanceof TdApi.AuthorizationStateWaitCode) {
            stage = Stage.AUTH_CODE;
            logger.info("[AUTH] AuthorizationStateWaitCode: {}", update);
        } else if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            stage = Stage.AUTHENTICATED;
            telegramError = Optional.empty();
            logger.info("[AUTH] AuthorizationStateReady: {}", update);
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            System.out.println("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out...");
        } else {
            logger.debug("[AUTH] {}", update);
        }
    }

    public boolean isReady() {
        return stage == Stage.AUTHENTICATED;
    }
}
