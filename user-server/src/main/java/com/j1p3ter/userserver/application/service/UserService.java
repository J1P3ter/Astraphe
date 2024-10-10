package com.j1p3ter.userserver.application.service;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.userserver.application.dto.*;
import com.j1p3ter.userserver.domain.exception.IdentityVerificationFailedException;
import com.j1p3ter.userserver.domain.exception.LoginIdDuplicatedException;
import com.j1p3ter.userserver.domain.exception.PasswordNotMatchesException;
import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.repository.UserRepository;
import com.j1p3ter.userserver.infrastructure.jwt.JwtUtil;
import com.j1p3ter.userserver.presentation.request.LogInRequestDto;
import com.j1p3ter.userserver.presentation.request.SignUpRequestDto;
import com.j1p3ter.userserver.presentation.request.UserUpdateRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
            validateLoginId(signUpRequestDto.getLoginId());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "loginId가 중복되었습니다.", e.getMessage());
        }

        // [2] password 암호화
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

        // [3] 회원가입
        User user = signUpRequestDto.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        // [4] 응답 반환
        return SignUpResponseDto.fromEntity(savedUser);
    }

    @Transactional
    public LogInResponseDto logIn(LogInRequestDto logInRequestDto, HttpServletResponse response) {

        // [1] loginId 검증
        try {
            userRepository.findByLoginId(logInRequestDto.getLoginId()).orElseThrow();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "loginId가 일치하지 않습니다.", e.getMessage());
        }

        // [2] password 검증
        String password = logInRequestDto.getPassword();
        User user = userRepository.findByLoginId(logInRequestDto.getLoginId()).get();
        try {
            validatePassword(password, user.getPassword());
        } catch (PasswordNotMatchesException e){
            throw new ApiException(HttpStatus.BAD_REQUEST, "password가 일치하지 않습니다.", e.getMessage());
        }

        // [3] login 성공 시 accessToken 발급
        String accessToken = jwtUtil.createToken(user.getId(), user.getUserRole());
        response.setHeader("Authorization", accessToken);

        // [4] 응답 반환
        return LogInResponseDto.fromString(accessToken);
    }

    @Transactional
    public UserGetResponseDto getUserInfo(Long xUserId, Long userId) {

        // [1] X-User-ID 헤더와 userId가 같은지 검증
        User user = validateUserIdMatchingAndExistence(xUserId, userId);

        // [2] 응답 반환
        return UserGetResponseDto.fromEntity(user);
    }

    @Transactional
    public UserUpdateResponseDto updateUserInfo(Long xUserId, Long userId, UserUpdateRequestDto userUpdateRequestDto) {

        // [1] X-User-ID 헤더와 userId가 같은지 검증
        User user = validateUserIdMatchingAndExistence(xUserId, userId);

        // [2] userUpdateRequestDto에 password 값이 있을 경우 해당 password를 암호화
        String password = null;
        if (StringUtils.hasText(userUpdateRequestDto.getPassword())) {
            password = passwordEncoder.encode(userUpdateRequestDto.getPassword());
        }

        // [3] user 수정
        user.update(userUpdateRequestDto, password);

        // [4] 응답 반환
        return UserUpdateResponseDto.fromEntity(user);
    }

    @Transactional
    public UserDeleteResponseDto deleteUser(Long xUserId, Long userId) {

        // [1] X-User-ID 헤더와 userId가 같은지 검증
        User user = validateUserIdMatchingAndExistence(xUserId, userId);

        // [2] soft delete
        userRepository.delete(user);

        // [3] 응답 반환
        return UserDeleteResponseDto.fromEntity(user);
    }

    private void validateLoginId(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new LoginIdDuplicatedException();
        }
    }

    private void validatePassword(String plainPassword, String encodedPassword) {
        if (!passwordEncoder.matches(plainPassword, encodedPassword)) {
            throw new PasswordNotMatchesException();
        }
    }

    private void validateIdentity(Long xUserId, Long userId) {
        if (!xUserId.equals(userId)) {
            throw new IdentityVerificationFailedException();
        }
    }

    private User validateUserIdMatchingAndExistence(Long xUserId, Long userId) {

        // [1] xUserId와 userId가 일치하는지 검증
        try {
            validateIdentity(xUserId, userId);
        } catch (IdentityVerificationFailedException e) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 정보만 조회할 수 있습니다.", e.getMessage());
        }

        // [2] userId 존재 여부 검증
        try {
            return userRepository.findById(userId).orElseThrow();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "userId가 존재하지 않습니다.", e.getMessage());
        }
    }

}