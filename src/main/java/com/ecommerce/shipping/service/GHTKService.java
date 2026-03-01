package com.ecommerce.shipping.service;

import com.ecommerce.shipping.dto.response.ShippingFeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
public class GHTKService {
    private final WebClient webClient;
    private final String ghtkApiKey;

    public GHTKService(WebClient webClient, @Value("${ghtk.api-key}") String ghtkApiKey) {
        this.webClient = webClient;
        this.ghtkApiKey = ghtkApiKey;
    }

    public ShippingFeeResponse calculateShippingFee(String pickupAddress, String deliveryAddress, Integer weight) {
        try {
            log.info("Calculating GHTK shipping fee");

            Map<String, Object> body = Map.of(
                    "pick_address_id", pickupAddress,
                    "deliver_address_id", deliveryAddress,
                    "weight", weight,
                    "type", 1
            );

            GHTKFeeResponse response = webClient.post()
                    .uri("https://api.ghtk.vn/v2/shipping/fee")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header("Token", ghtkApiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GHTKFeeResponse.class)
                    .block();

            if (response != null && response.isSuccess()) {
                return ShippingFeeResponse.builder()
                        .fee(new BigDecimal(response.getData().getFee()))
                        .estimatedDays(response.getData().getEstimatedDeliveryTime())
                        .provider("GHTK")
                        .message("Success")
                        .build();
            }

            return ShippingFeeResponse.builder()
                    .fee(BigDecimal.ZERO)
                    .provider("GHTK")
                    .message("Failed to calculate fee")
                    .build();

        } catch (Exception e) {
            log.error("Error calculating GHTK shipping fee", e);
            throw new RuntimeException("Failed to calculate GHTK shipping fee", e);
        }
    }

    public String createOrder(Map<String, Object> orderData) {
        try {
            log.info("Creating GHTK order");

            GHTKOrderResponse response = webClient.post()
                    .uri("https://api.ghtk.vn/v2/shipping/create")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header("Token", ghtkApiKey)
                    .bodyValue(orderData)
                    .retrieve()
                    .bodyToMono(GHTKOrderResponse.class)
                    .block();

            if (response != null && response.isSuccess()) {
                return response.getData().getOrderCode();
            }

            throw new RuntimeException("Failed to create GHTK order");

        } catch (Exception e) {
            log.error("Error creating GHTK order", e);
            throw new RuntimeException("Failed to create GHTK order", e);
        }
    }

    public Map<String, Object> getTrackingInfo(String orderCode) {
        try {
            log.info("Getting GHTK tracking info for order: {}", orderCode);

            GHTKTrackingResponse response = webClient.get()
                    .uri("https://api.ghtk.vn/v2/shipping/detail?order_code={orderCode}", orderCode)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header("Token", ghtkApiKey)
                    .retrieve()
                    .bodyToMono(GHTKTrackingResponse.class)
                    .block();

            if (response != null && response.isSuccess()) {
                return response.getTrackingData();
            }

            throw new RuntimeException("Failed to get GHTK tracking info");

        } catch (Exception e) {
            log.error("Error getting GHTK tracking info", e);
            throw new RuntimeException("Failed to get GHTK tracking info", e);
        }
    }

    // Inner DTOs for GHTK API responses
    public static class GHTKFeeData {
        private int fee;
        private int estimatedDeliveryTime;

        public int getFee() { return fee; }
        public void setFee(int fee) { this.fee = fee; }
        public int getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
        public void setEstimatedDeliveryTime(int estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    }

    public static class GHTKFeeResponse {
        private boolean success;
        private String message;
        private GHTKFeeData data;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public GHTKFeeData getData() { return data; }
        public void setData(GHTKFeeData data) { this.data = data; }
    }

    public static class GHTKOrderData {
        private String orderCode;
        private String status;

        public String getOrderCode() { return orderCode; }
        public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class GHTKOrderResponse {
        private boolean success;
        private String message;
        private GHTKOrderData data;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public GHTKOrderData getData() { return data; }
        public void setData(GHTKOrderData data) { this.data = data; }
    }

    public static class GHTKTrackingResponse {
        private boolean success;
        private String message;
        private Map<String, Object> data;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getTrackingData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }
}

