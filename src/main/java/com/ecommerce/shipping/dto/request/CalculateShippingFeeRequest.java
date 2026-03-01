package com.ecommerce.shipping.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculateShippingFeeRequest implements Serializable {
    @NotBlank(message = "From address cannot be blank")
    private String fromAddress;

    @NotBlank(message = "To address cannot be blank")
    private String toAddress;

    @NotNull(message = "Weight cannot be null")
    @Positive(message = "Weight must be positive")
    private Integer weight;

    @NotBlank(message = "From district ID cannot be blank")
    private String fromDistrictId;

    @NotBlank(message = "To district ID cannot be blank")
    private String toDistrictId;
}

