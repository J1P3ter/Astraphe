package com.j1p3ter.userserver.application.unittest;

import com.j1p3ter.userserver.application.service.UserService;
import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.repository.UserRepository;
import com.j1p3ter.userserver.infrastructure.jwt.JwtUtil;
import com.j1p3ter.userserver.presentation.request.SignUpRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @DisplayName("회원 가입 테스트")
    @Test
    void test1() {
        // given
        String requestLoginId = "loginId1";
        String requestPassword = "Password!234";
        String requestUsername = "username1";
        String requestNickname = "nickname1";
        String requestPhoneNumber = "010-1111-1111";
        String requestUserRole = "CUSTOMER";

        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(requestLoginId, requestPassword, requestUsername, requestNickname, requestPhoneNumber, requestUserRole);
        String encodedPassword = passwordEncoder.encode(requestPassword);

        // when
        User user = signUpRequestDto.toEntity(encodedPassword);
        doReturn(user)
                .when(userRepository).save(any(User.class));

        // then
        assertThat(userService.createUser(signUpRequestDto).getLoginId()).isEqualTo(user.getLoginId());
    }


}
