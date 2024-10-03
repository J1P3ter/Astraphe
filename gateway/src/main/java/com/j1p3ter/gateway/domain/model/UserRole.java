package com.j1p3ter.gateway.domain.model;

public enum UserRole {
    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    SELLER("SELLER"),
    CUSTOMER("CUSTOMER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    private String getAuthority() {
        return this.authority;
    }

}