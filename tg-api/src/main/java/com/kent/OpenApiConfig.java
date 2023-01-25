package com.kent;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        String fullPageName = "com.kent";
        String groupName = "Telgram服務";
        return new OpenAPI()
                .info(new Info()
                                .title(groupName)
                                .description(groupName)
                                .version("v1.0")
//                        .contact(new Contact()
//                                .name("Arun")
//                                .url("https://asbnotebook.com")
//                                .email("asbnotebook@gmail.com"))
//                        .termsOfService("TOC")
//                        .license(new License().name("License").url("#"))
                );
    }
}