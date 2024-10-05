package com.j1p3ter.userserver.application.service;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.userserver.application.dto.SignUpResponseDto;
import com.j1p3ter.userserver.infrastructure.jwt.JwtUtil;
import com.j1p3ter.userserver.presentation.request.LogInRequestDto;
import com.j1p3ter.userserver.presentation.request.SignUpRequestDto;
import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.repository.UserRepository;
import com.j1p3ter.userserver.presentation.response.CommonApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public SignUpResponseDto createUser(SignUpRequestDto signUpRequestDto) {

        // [1] 중복 loginId 검증
        try {
            userRepository.findByLoginId(signUpRequestDto.getLoginId());
        }catch (Exception e){
            throw new ApiException(HttpStatus.BAD_REQUEST, "loginId가 중복되었습니다.", e.getMessage());
        }

        // [2] password 암호화
        String password = passwordEncoder.encode(signUpRequestDto.getPassword());

        // [3] 회원가입
        User user = signUpRequestDto.toEntity(password);
        User savedUser = userRepository.save(user);

        // [4] 응답 반환
        return SignUpResponseDto.fromEntity(savedUser);
    }

    public String logIn(LogInRequestDto logInRequestDto, HttpServletResponse response) {

        // [1] loginId 검증
        try {
            userRepository.findByLoginId(logInRequestDto.getLoginId());
        } catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "loginId가 일치하지 않습니다.", e.getMessage());
        }

        // [2] password 검증
        String password = logInRequestDto.getPassword();
        User user = userRepository.findByLoginId(logInRequestDto.getLoginId()).get();
        try {
            passwordEncoder.matches(password, user.getPassword());
        } catch (Exception e){
            throw new ApiException(HttpStatus.NOT_FOUND, "password가 일치하지 않습니다.", e.getMessage());
        }

        // [3] login 성공 시 accessToken 발급
        String accessToken = jwtUtil.createToken(user.getUsername(), user.getUserRole());
        response.setHeader("Authorization", accessToken);

        // [4] 응답 반환
        return accessToken;
    }
}
