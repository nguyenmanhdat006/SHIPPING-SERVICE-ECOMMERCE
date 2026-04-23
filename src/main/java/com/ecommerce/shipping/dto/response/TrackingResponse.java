package com.ecommerce.shipping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingResponse {
    private String trackingNumber;
    private String status;
    private String currentLocation;
    private LocalDateTime expectedDelivery;
    private List<TrackingEvent> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrackingEvent {
        private String status;
        private String location;
        private LocalDateTime timestamp;
        private String description;
    }
}

