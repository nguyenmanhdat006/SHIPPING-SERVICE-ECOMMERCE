package com.ecommerce.shipping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingFeeResponse implements Serializable {
    private BigDecimal fee;
    private Integer estimatedDays;
    private String provider;
    private String message;
}

