package com.kent.tg.client;

import com.kent.base.domain.DomainUtil;
import com.kent.tg.domain.dto.AuthInputDto;
import it.tdlight.client.*;
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
import java.util.HashMap;
import java.util.Map;
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

        client.addDefaultExceptionHandler(e -> {
            logger.error("error", e);
            if (e instanceof TelegramError error) {
                client.sendClose();
                fileUtils.deleteFileByName(TgFileUtils.CONFIG_FILE);
                fileUtils.deleteFileByName(TgFileUtils.BINLOG_FILE);
                eventPublisher.publishEvent(error);
            }
        });

        logger.info("Authentication with Phone Number: [{}]", phoneNumber);

        var authenticationData = AuthenticationData.user(phoneNumber);
        // Start the client
        client.start(authenticationData);
        logger.info("Started and Waiting");

        client.waitForExit();
    }

    public Result<TdApi.File> syncDownload(TdApi.File file) {
        int priority = 1;
        int offset = 0;
        int limit = 0;
        boolean synchronous = true;
        TdApi.DownloadFile request = new TdApi.DownloadFile(file.id, priority, offset, limit, synchronous);
        logger.info("Send download file request: {} , file: {}", DomainUtil.convertPojoToMap(request), DomainUtil.convertPojoToMap(file));
        return sendSynchronously(request, TdApi.File.class, 30);
    }

    /**
     * Note: asyncDownload may trigger some kind of listener which listening to TdApi.UpdateFile Event. ex: UpdateFileListener
     *
     * @param file
     */
    public void asyncDownload(TdApi.File file) {
        int priority = 1;
        int offset = 0;
        int limit = 0;
        boolean synchronous = false;
        TdApi.DownloadFile request = new TdApi.DownloadFile(file.id, priority, offset, limit, synchronous);
        logger.info("Send download file request: {} , file: {}", DomainUtil.convertPojoToMap(request), DomainUtil.convertPojoToMap(file));
        sendAsynchronously(request);
    }

    public void asyncDownloadByFileId(int fileId) {
        int priority = 1;
        int offset = 0;
        int limit = 0;
        boolean synchronous = false;
        TdApi.DownloadFile request = new TdApi.DownloadFile(fileId, priority, offset, limit, synchronous);
        sendAsynchronously(request);
    }

    public Result<TdApi.Message> getMessage(long messageId, long chatId) {
        var request = new TdApi.GetMessage(chatId, messageId);
        Result<TdApi.Message> result = sendSynchronously(request, TdApi.Message.class, 30);
        if (result.isError()) {
            logger.error("Get Message fail. {}", LoggerUtils.shrinkText(result.getError().toString(), 1000));
        } else {
            TdApi.Message message = result.get();
            logger.warn("Message which have missing file: {}", LoggerUtils.shrinkText(message.toString(), 1000));
        }
        return result;
    }

    public Map<Long, TdApi.Chat> listChats() {
        Map<Long, TdApi.Chat> chatMap = new HashMap<>();
        TdApi.GetChats request = new TdApi.GetChats(new TdApi.ChatListMain(), 100);
        Result<TdApi.Chats> result = sendSynchronously(request, TdApi.Chats.class, 10);
        if (!result.isError()) {
            long[] chatIds = result.get().chatIds;
            logger.info("Fetch MainChatIds: {}", chatIds);

            for (long chatId : chatIds) {

                if (!chatMap.containsKey(chatId)) {
                    Result<TdApi.Chat> result2 = sendSynchronously(new TdApi.GetChat(chatId), TdApi.Chat.class, 10);
                    if (result.isError()) {
                        logger.error("Receive an error for GetChats: " + result2.getError());
                    } else {
                        chatMap.put(chatId, result2.get());
                    }
                }
            }

        } else {
            logger.error("Fetch MainChatIds fail: {}", result.error());
        }
        return chatMap;
    }

    public Result<TdApi.Chat> getChat(Long chatId) {
        var request = new TdApi.GetChat(chatId);
        Result<TdApi.Chat> chatResult = sendSynchronously(request, TdApi.Chat.class, 30);
        return chatResult;
    }

    public Result<TdApi.Object> getAuthCode(AuthInputDto dto) {
        TdApi.CheckAuthenticationCode request = new TdApi.CheckAuthenticationCode(dto.getCode());
        Result<TdApi.Object> objectResult = sendSynchronously(request, TdApi.Object.class, 30);
        return objectResult;
    }

    /**
     * NOTE: If possible, use high-level APIs in this class before resorting to this API.
     * NOTICE: This API may be block out until request timeout.
     *
     * @param request
     * @param resultType
     * @param timeoutSeconds
     * @param <T>
     * @return
     */
    private <T extends TdApi.Object> Result<T> sendSynchronously(TdApi.Function request, Class<T> resultType, int timeoutSeconds) {
        var response = new CompletableFuture<>();
        logger.info("[SEND SYNC] ==> {}", LoggerUtils.shrinkText(request.toString(), 1000));
        client.send(request, response::complete);
        try {
            return (Result<T>) response.completeOnTimeout(new TdApi.Error(408, "Request Timeout"), timeoutSeconds, TimeUnit.SECONDS).get();
        } catch (InterruptedException e) {
            return Result.ofError(e);
        } catch (ExecutionException e) {
            return Result.ofError(e);
        }
    }

    private void sendAsynchronously(TdApi.Function request) {
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

    private void sendAsynchronously(TdApi.Function request, GenericResultHandler resultHandler) {
        client.send(request, resultHandler);
    }

}