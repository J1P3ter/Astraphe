package com.j1p3ter.userserver.application.service;

import com.j1p3ter.userserver.application.dto.UserCreateResponse;
import com.j1p3ter.userserver.presentation.request.SignUpRequest;
import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.repository.UserRepository;
import com.j1p3ter.userserver.presentation.response.CommonApiResponse;
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

        // [4] 응답
        return new CommonApiResponse(200, UserCreateResponse.fromEntity(savedUser), null, LocalDateTime.now());
    }
}
