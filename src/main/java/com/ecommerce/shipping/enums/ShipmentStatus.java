package com.ecommerce.shipping.enums;

public enum ShipmentStatus {
    PENDING("Pending"),
    PICKED_UP("Picked Up"),
    IN_TRANSIT("In Transit"),
    OUT_FOR_DELIVERY("Out For Delivery"),
    DELIVERED("Delivered"),
    FAILED_DELIVERY("Failed Delivery"),
    CANCELLED("Cancelled");

    private final String description;

    ShipmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

