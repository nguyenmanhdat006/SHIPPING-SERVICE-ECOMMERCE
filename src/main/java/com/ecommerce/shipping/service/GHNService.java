package com.ecommerce.shipping.service;

import com.ecommerce.shipping.dto.response.ShippingFeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class GHNService {
    private final WebClient webClient;
    private final String ghnApiKey;

    public GHNService(WebClient webClient, @Value("${ghn.api-key}") String ghnApiKey) {
        this.webClient = webClient;
        this.ghnApiKey = ghnApiKey;
    }

    public ShippingFeeResponse calculateShippingFee(
            String fromDistrictId, String toDistrictId, Integer weight) {
        try {
            log.info("Calculating GHN shipping fee from {} to {}", fromDistrictId, toDistrictId);

            Map<String, Object> body = Map.of(
                    "from_district_id", Integer.parseInt(fromDistrictId),
                    "to_district_id", Integer.parseInt(toDistrictId),
                    "weight", weight,
                    "service_id", 2
            );

            GHNFeeResponse response = webClient.post()
                    .uri("https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header("Token", ghnApiKey)
                    .header("ShopId", "0")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GHNFeeResponse.class)
                    .block();

            if (response != null && response.getCode() == 200) {
                Object feeObj = response.getData().get("total");
                BigDecimal fee = feeObj != null ? new BigDecimal(feeObj.toString()) : BigDecimal.ZERO;
                return ShippingFeeResponse.builder()
                        .fee(fee)
                        .estimatedDays(3)
                        .provider("GHN")
                        .message("Success")
                        .build();
            }

            return ShippingFeeResponse.builder()
                    .fee(BigDecimal.ZERO)
                    .provider("GHN")
                    .message("Failed to calculate fee")
                    .build();

        } catch (Exception e) {
            log.error("Error calculating GHN shipping fee", e);
            throw new RuntimeException("Failed to calculate GHN shipping fee", e);
        }
    }

    public String createOrder(Map<String, Object> orderData) {
        try {
            log.info("Creating GHN order");

            GHNOrderResponse response = webClient.post()
                    .uri("https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header("Token", ghnApiKey)
                    .header("ShopId", "0")
                    .bodyValue(orderData)
                    .retrieve()
                    .bodyToMono(GHNOrderResponse.class)
                    .block();

            if (response != null && response.getCode() == 200) {
                return response.getData().get("order_code").toString();
            }

            throw new RuntimeException("Failed to create GHN order");

        } catch (Exception e) {
            log.error("Error creating GHN order", e);
            throw new RuntimeException("Failed to create GHN order", e);
        }
    }

    public Map<String, Object> getTrackingInfo(String orderCode) {
        try {
            log.info("Getting GHN tracking info for order: {}", orderCode);

            Map<String, Object> body = Map.of("order_code", orderCode);

            GHNTrackingResponse response = webClient.post()
                    .uri("https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/detail")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header("Token", ghnApiKey)
                    .header("ShopId", "0")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GHNTrackingResponse.class)
                    .block();

            if (response != null && response.getCode() == 200) {
                return response.getData();
            }

            throw new RuntimeException("Failed to get GHN tracking info");

        } catch (Exception e) {
            log.error("Error getting GHN tracking info", e);
            throw new RuntimeException("Failed to get GHN tracking info", e);
        }
    }

    // Inner DTOs for GHN API responses
    public static class GHNFeeResponse {
        private int code;
        private String message;
        private Map<String, Object> data;

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }

    public static class GHNOrderResponse {
        private int code;
        private String message;
        private Map<String, Object> data;

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }

    public static class GHNTrackingResponse {
        private int code;
        private String message;
        private Map<String, Object> data;

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }
}

