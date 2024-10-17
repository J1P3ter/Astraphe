package com.j1p3ter.userserver.infrastructure.repository;

import com.j1p3ter.userserver.application.dto.UserGetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryDslUserRepository {
    Page<UserGetResponseDto> findUsersBy(Pageable pageable);
}
