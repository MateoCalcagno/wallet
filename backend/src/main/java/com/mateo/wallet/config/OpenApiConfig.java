package com.mateo.wallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI walletApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet API")
                        .description("Virtual wallet built with Spring Boot")
                        .version("1.0.0"));
    }
}