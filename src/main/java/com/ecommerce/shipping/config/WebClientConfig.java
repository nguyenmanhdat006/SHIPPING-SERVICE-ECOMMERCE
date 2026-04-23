package com.ecommerce.shipping.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final GHNConfig ghnConfig;

    @Bean
    public WebClient ghnWebClient() {
        return WebClient.builder()
                .baseUrl(ghnConfig.getApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Token", ghnConfig.getToken())
                .defaultHeader("ShopId", ghnConfig.getShopId())
                .build();
    }
}

