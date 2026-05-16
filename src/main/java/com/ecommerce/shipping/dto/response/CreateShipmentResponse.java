package com.ecommerce.shipping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentResponse {
    private Long shipmentId;
    private String shipmentNumber;
    private String orderId;
    private String orderNumber;
    private String status;
    private BigDecimal shippingFee;
    private BigDecimal codAmount;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime createdAt;
}

