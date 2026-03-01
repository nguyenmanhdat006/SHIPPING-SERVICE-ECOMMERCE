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
    private String id;
    private String orderId;
    private String trackingNumber;
    private String provider;
    private String status;
    private BigDecimal shippingFee;
    private String fromAddress;
    private String toAddress;
    private String recipientName;
    private String recipientPhone;
    private Integer weight;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

