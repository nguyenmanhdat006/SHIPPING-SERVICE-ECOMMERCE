package com.ecommerce.shipping.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculateFeeRequest {

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Province is required")
    private String province;

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be at least 1 gram")
    private Integer weight;

    @NotNull(message = "Order value is required")
    @DecimalMin(value = "0", message = "Order value must be at least 0")
    private BigDecimal orderValue;
}

