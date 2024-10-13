package com.j1p3ter.userserver.application.integrationtest;

import com.j1p3ter.userserver.application.dto.UserDeleteResponseDto;
import com.j1p3ter.userserver.application.dto.UserUpdateResponseDto;
import com.j1p3ter.userserver.application.service.UserService;
import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.repository.UserRepository;
import com.j1p3ter.userserver.presentation.request.SignUpRequestDto;
import com.j1p3ter.userserver.presentation.request.UserUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private SignUpRequestDto signUpRequestDto;
    private UserUpdateRequestDto userUpdateRequestDto;

    @BeforeEach
    void setup() {
        signUpRequestDto = new SignUpRequestDto(
                "loginId1",
                "Password!234",
                "testUsername",
                "testNickname",
                "010-1111-1111",
                "testShippingAddress",
                "CUSTOMER"
        );

        userUpdateRequestDto = new UserUpdateRequestDto(
                "newUsername",
                "newNickname",
                "newPassword!234",
                "newShippingAddress",
                null
        );
    }

    @Test
    @DisplayName("사용자 정보 수정 테스트")
    void test1() {
        // given
        User user = signUpRequestDto.toEntity(passwordEncoder.encode(signUpRequestDto.getPassword()));
        userRepository.save(user);

        // when
        UserUpdateResponseDto responseDto = userService.updateUserInfo(user.getId(), user.getId(), userUpdateRequestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getNickname()).isEqualTo(userUpdateRequestDto.getNickname());
    }

    @Test
    @DisplayName("사용자 정보 삭제 테스트")
    void test2() {
        // given
        User user = signUpRequestDto.toEntity(passwordEncoder.encode(signUpRequestDto.getPassword()));
        userRepository.save(user);

        // when
        UserDeleteResponseDto responseDto = userService.deleteUser(user.getId(), user.getId());

        // then
        assertThat(responseDto.getDeletedAt()).isBefore(LocalDateTime.now());
    }
}