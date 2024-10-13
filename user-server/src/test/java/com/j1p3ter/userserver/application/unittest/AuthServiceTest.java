package com.j1p3ter.userserver.application.unittest;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.userserver.application.service.UserService;
import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.repository.UserRepository;
import com.j1p3ter.userserver.presentation.request.SignUpRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @DisplayName("회원 가입 성공 테스트")
    @Test
    void test1() {

        // given
        String requestPassword = "Password!234";
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("loginId1", requestPassword, "username1", "nickname1", "010-1111-1111", "shippingAddress1", "CUSTOMER");
        String encodedPassword = passwordEncoder.encode(requestPassword);

        // when
        User user = signUpRequestDto.toEntity(encodedPassword);
        doReturn(user)
                .when(userRepository).save(any(User.class));

        // then
        assertThat(userService.createUser(signUpRequestDto).getLoginId()).isEqualTo(user.getLoginId());
    }

    @DisplayName("회원 가입 실패 테스트 - 중복 loginId 검증")
    @Test
    void test2() {

        // given
        String requestPassword = "Password!234";
        SignUpRequestDto signUpRequestDto1 = new SignUpRequestDto("loginId1", requestPassword, "username1", "nickname1", "010-1111-1111", "shippingAddress1", "CUSTOMER");
        User user1 = signUpRequestDto1.toEntity(passwordEncoder.encode(requestPassword));

        doReturn(Optional.of(user1))
                .when(userRepository).findByLoginId("loginId1");

        // when
        SignUpRequestDto signUpRequestDto2 = new SignUpRequestDto("loginId1", "Password123$", "username2", "nickname2", "010-2222-2222", "shippingAddress2", "CUSTOMER");
        ApiException exception = assertThrows(ApiException.class, () -> userService.createUser(signUpRequestDto2));

        // then
        assertThat(exception.getMessage()).contains("loginId가 중복되었습니다.");
    }

}
