package com.j1p3ter.userserver.domain.model;

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

    public static UserRole fromString(String authority) {
        for (UserRole userRole: UserRole.values()) {
            if (userRole.getAuthority().equalsIgnoreCase(authority)) {
                return userRole;
            }
        }

        throw new IllegalArgumentException("No enum constant for authority: " + authority);
    }

}
