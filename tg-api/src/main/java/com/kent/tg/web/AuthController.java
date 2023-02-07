package com.kent.tg.web;

import com.kent.tg.client.Telegram;
import com.kent.tg.daemon.AuthListener;
import com.kent.tg.domain.dto.AuthInputDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ServiceUnavailableException;
import java.util.Map;


@Tag(name = "Auth")
@RestController
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private Telegram telegram;
    @Autowired
    private AuthListener authListener;


    @GetMapping("/auth")
    public Map<String, Object> getAuthStage() {
        return authListener.basicInfo();
    }

    @PostMapping("/auth")
    public Map<String, Object> updatePhoneNumber(@RequestBody AuthInputDto dto) throws ServiceUnavailableException {
        if (StringUtils.isNotBlank(dto.getCode())) {
            Result<TdApi.Object> objectResult = telegram.getAuthCode(dto);
            if (objectResult.isError()) {
                var error = objectResult.error().get();
                return Map.of("error", error.message, "code", error.code);
            } else {
                return Map.of("result", objectResult.get());
            }
        } else if (StringUtils.isNoneBlank(dto.getPhoneNumber())) {
            Long phoneNumber = Long.parseLong(dto.getPhoneNumber());
            telegram.updatePhoneNumber(phoneNumber);
            try {
                telegram.start(phoneNumber);
            } catch (Exception e) {
                throw new ServiceUnavailableException();
            }

            Map<String, Object> result = authListener.basicInfo();
            result.put("phoneNumber", phoneNumber);
            return result;
        }
        return Map.of("message", "ok");
    }

}
