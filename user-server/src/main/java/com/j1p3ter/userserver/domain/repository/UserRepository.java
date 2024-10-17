package com.j1p3ter.userserver.domain.repository;

import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.infrastructure.repository.QueryDslUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, QueryDslUserRepository {
    Optional<User> findByLoginId(@Param("loginId") String loginId);
}
