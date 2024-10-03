package com.j1p3ter.userserver.application.service;

import com.j1p3ter.userserver.application.dto.UserDto;
import com.j1p3ter.userserver.infrastructure.jwt.JwtUtil;
import com.j1p3ter.userserver.presentation.request.LogInRequest;
import com.j1p3ter.userserver.presentation.request.SignUpRequest;
import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.repository.UserRepository;
import com.j1p3ter.userserver.presentation.response.CommonApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public CommonApiResponse createUser(SignUpRequest signUpRequest) {

        // [1] 중복 loginId 검증
        if (userRepository.findByLoginId(signUpRequest.getLoginId()).isPresent()) {
            return new CommonApiResponse(409, null, "loginId가 중복되었습니다.", LocalDateTime.now());
        }

        // [2] password 암호화
        String password = passwordEncoder.encode(signUpRequest.getPassword());

        // [3] 회원가입
        User user = signUpRequest.toEntity(password);
        User savedUser = userRepository.save(user);

        // [4] 응답 반환
        return new CommonApiResponse(200, UserDto.fromEntity(savedUser), null, LocalDateTime.now());
    }

    public CommonApiResponse logIn(LogInRequest logInRequest, HttpServletResponse response) {

        // [1] loginId 검증
        if (userRepository.findByLoginId(logInRequest.getLoginId()).isEmpty()) {
            return new CommonApiResponse(400, null, "loginId가 일치하지 않습니다.", LocalDateTime.now());
        }

        // [2] password 검증
        String password = logInRequest.getPassword();
        User user = userRepository.findByLoginId(logInRequest.getLoginId()).get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new CommonApiResponse(400, null, "password가 일치하지 않습니다.", LocalDateTime.now());
        }

        // [3] accessToken 발급
        String accessToken = jwtUtil.createToken(user.getUsername(), user.getUserRole());
        response.setHeader("Authorization", accessToken);

        // [4] 응답 반환
        return new CommonApiResponse(200, accessToken, null, LocalDateTime.now());
    }
}
