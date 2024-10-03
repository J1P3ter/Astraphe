package com.j1p3ter.userserver.domain.model;

import com.j1p3ter.common.auditing.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder // 빌더 패턴 협의
@Getter
@Entity
@Table(name = "tb_user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "phone_num", nullable = false)
    private String phoneNum;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Column(name = "slack_id", nullable = true)
    private Long slackId;

}