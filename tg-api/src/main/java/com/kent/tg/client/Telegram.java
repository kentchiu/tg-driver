package com.kent.tg.client;

import com.kent.base.domain.DomainUtil;
import it.tdlight.client.*;
import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class Telegram implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(Telegram.class);
    private SimpleTelegramClient client;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    private TgFileUtils fileUtils;

    @Value("${kent.tg.home}")
    public void setTgHome(Path tgHome) {
        fileUtils = TgFileUtils.getInstance();
        fileUtils.init(tgHome);
    }

    public void updatePhoneNumber(Long phoneNumber) {
        fileUtils.deleteFileByName(TgFileUtils.DB_FILE);
        fileUtils.deleteFileByName(TgFileUtils.BINLOG_FILE);

        Path configFile = fileUtils.getPathByName(TgFileUtils.CONFIG_FILE);
        Properties p = new Properties();
        if (Files.exists(configFile)) {
            try {
                p.load(Files.newInputStream(configFile));
            } catch (IOException e) {
                String msg = "Load properties file fail";
                logger.error(msg, e);
                throw new RuntimeException(msg);
            }
        }
        p.setProperty("phoneNumber", phoneNumber.toString());
        try {
            p.store(Files.newOutputStream(configFile), "tg-api config file");
            logger.info("Update phoneNumber to {}", phoneNumber);
        } catch (IOException e) {
            String msg = "Save properties file fail";
            logger.error(msg, e);
            throw new RuntimeException(msg);
        }
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Path configFile = fileUtils.getPathByName(TgFileUtils.CONFIG_FILE);

        if (Files.exists(configFile)) {
            logger.info("load properties file from {}", configFile);
            Properties p = new Properties();
            try {
                p.load(Files.newInputStream(configFile));
                var phoneNumber = Long.parseLong(p.getProperty("phoneNumber"));
                logger.info("Start Telegram with phone number: {}", phoneNumber);
                start(phoneNumber);
            } catch (IOException | CantLoadLibrary | InterruptedException e) {
                String msg = "Start server with new phone number fail";
                logger.error(msg, e);
            }
        } else {
            logger.warn("properties file not created yet: {}", configFile);
        }
    }

    public void start(long phoneNumber) throws CantLoadLibrary, InterruptedException {
        logger.info("Start Init Telegram Client");
        // Initialize TDLight native libraries
        Init.start();

        var apiToken = APIToken.example();

        // Configure the client
        var settings = TDLibSettings.create(apiToken);

        // Configure the session directory
        Path sessionPath = fileUtils.getPathByName(TgFileUtils.SESSION_HOME);
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));
        settings.setEnableStorageOptimizer(false);

        // Create a client
        client = new SimpleTelegramClient(settings);


        // Add an example update handler that prints when the bot is started
        client.addUpdateHandler(TdApi.UpdateAuthorizationState.class, update -> eventPublisher.publishEvent(update));

        // Add an example update handler that prints every received message
        client.addUpdateHandler(TdApi.UpdateNewMessage.class, update -> eventPublisher.publishEvent(update));

        client.addUpdateHandler(TdApi.UpdateFile.class, update -> eventPublisher.publishEvent(update));
        client.addUpdateHandler(TdApi.UpdateDeleteMessages.class, update -> eventPublisher.publishEvent(update));

        client.addDefaultExceptionHandler(new ExceptionHandler() {
            @Override
            public void onException(Throwable e) {
                logger.error("error", e);
                if (e instanceof TelegramError error) {
                    client.sendClose();
                    fileUtils.deleteFileByName(TgFileUtils.CONFIG_FILE);
                    fileUtils.deleteFileByName(TgFileUtils.BINLOG_FILE);
                    eventPublisher.publishEvent(error);
                }
            }
        });

        logger.info("Authentication with Phone Number: [{}]", phoneNumber);

        var authenticationData = AuthenticationData.user(phoneNumber);
        // Start the client
        client.start(authenticationData);
        logger.info("Started and Waiting");

        client.waitForExit();
    }

    // NOTE：會卡住， 慎用
    public <T extends TdApi.Object> Result<T> sendSynchronously(TdApi.Function request, Class<T> resultType, int timeoutSeconds) {
        var response = new CompletableFuture<>();
        logger.info("[SEND SYNC] ==> {}, {}", request, timeoutSeconds);
        client.send(request, response::complete);
        try {
            return (Result<T>) response.completeOnTimeout(new TdApi.Error(408, "Request Timeout"), timeoutSeconds, TimeUnit.SECONDS).get();
        } catch (InterruptedException e) {
            return Result.ofError(e);
        } catch (ExecutionException e) {
            return Result.ofError(e);
        }
    }

    public void sendAsynchronously(TdApi.Function request) {
        GenericResultHandler<TdApi.Object> objectGenericResultHandler = result -> {
            if (result.isError()) {
                var object = result.get();
                logger.error("[SEND ASYNC] <==  [{}] => {} - {}", DomainUtil.convertPojoToMap(request), object.getClass().getSimpleName(), DomainUtil.convertPojoToMap(object));
            } else {

                var object = result.get();
                logger.info("[SEND ASYNC] <==  [{}] => {} - {}", DomainUtil.convertPojoToMap(request), object.getClass().getSimpleName(), DomainUtil.convertPojoToMap(object));
            }
        };
        sendAsynchronously(request, objectGenericResultHandler);
    }

    public void sendAsynchronously(TdApi.Function request, GenericResultHandler resultHandler) {
        client.send(request, resultHandler);
    }


}