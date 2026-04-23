package com.ecommerce.shipping.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculateFeeRequest {

    @NotNull(message = "District ID is required")
    private Integer toDistrictId;

    @NotBlank(message = "Ward code is required")
    private String toWardCode;

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be at least 1 gram")
    private Integer weight;

    @Builder.Default
    private Integer serviceTypeId = 2;
}

