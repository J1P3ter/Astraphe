package com.j1p3ter.productserver.application.dto;

public enum SortType {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    DISCOUNTED_PRICE("discountedPrice");

    private final String value;

    SortType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
