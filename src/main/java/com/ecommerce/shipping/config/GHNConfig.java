package com.ecommerce.shipping.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GHNConfig {

    @Value("${ghn.api-url}")
    private String apiUrl;

    @Value("${ghn.token}")
    private String token;

    @Value("${ghn.shop-id}")
    private String shopId;

    @Value("${ghn.from-district-id}")
    private Integer fromDistrictId;
}

