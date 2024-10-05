package com.j1p3ter.gateway.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {

    private Long id;
    private String loginId;
    private String password;
    private String username;
    private String nickname;
    private String phoneNumber;
    private UserRole userRole;
    private Long slackId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private boolean isDeleted = false;

}