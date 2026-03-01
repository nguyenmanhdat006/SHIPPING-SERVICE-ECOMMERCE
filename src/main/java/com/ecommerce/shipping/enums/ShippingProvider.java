package com.ecommerce.shipping.enums;

public enum ShippingProvider {
    GHN("Giao Hàng Nhanh"),
    GHTK("Giao Hàng Tiết Kiệm");

    private final String name;

    ShippingProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

