package com.j1p3ter.userserver.domain.model;

import com.j1p3ter.common.auditing.BaseEntity;
import com.j1p3ter.userserver.presentation.request.UserUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "tb_user")
@SQLDelete(sql = "UPDATE tb_user u SET u.is_deleted = true WHERE u.id = ?")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "login_id", nullable = false, updatable = false)
    private String loginId;

    @Column(name = "password", nullable = false, updatable = true)
    private String password;

    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    @Column(name = "nickname", nullable = false, updatable = true)
    private String nickname;

    @Column(name = "phone_number", nullable = false, updatable = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, updatable = true)
    private UserRole userRole;

    @Column(name = "slack_id", nullable = true, updatable = true)
    private Long slackId;

    /**
     * User 수정 비즈니스 로직
     *  - UserUpdateRequestDto의 필드 중 값이 존재하는 경우에만 userEntity의 필드 값을 수정합니다.
     * @param userUpdateRequestDto
     * @param password
     */
    public void update(UserUpdateRequestDto userUpdateRequestDto, String password) {
        if (userUpdateRequestDto.getNickname() != null) {
            this.nickname = userUpdateRequestDto.getNickname();
        }
        if (userUpdateRequestDto.getPhoneNumber() != null) {
            this.phoneNumber = userUpdateRequestDto.getPhoneNumber();
        }
        if (userUpdateRequestDto.getSlackId() != null) {
            this.slackId = userUpdateRequestDto.getSlackId();
        }
        if (password != null) {
            this.password = password;
        }
    }
}