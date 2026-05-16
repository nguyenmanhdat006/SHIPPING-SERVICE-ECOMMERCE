package com.ecommerce.shipping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponse implements Serializable {
    private Long id;
    private String orderId;
    private String orderNumber;
    private String status;
    private BigDecimal shippingFee;
    private BigDecimal codAmount;
    private String recipientName;
    private String recipientPhone;
    private String address;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

