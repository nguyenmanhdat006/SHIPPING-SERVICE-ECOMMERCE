package com.ecommerce.shipping.enums;

public enum ShippingProvider {
    GHN("Giao Hang Nhanh");

    private final String name;

    ShippingProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

