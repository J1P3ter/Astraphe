package com.j1p3ter.userserver.infrastructure.repository;

import com.j1p3ter.userserver.application.dto.UserGetResponseDto;
import com.j1p3ter.userserver.domain.model.UserRole;
import com.j1p3ter.userserver.infrastructure.configuration.QueryDslUtil;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.j1p3ter.userserver.domain.model.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QueryDslUserRepositoryImpl implements QueryDslUserRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<UserGetResponseDto> findUsersBy(Pageable pageable) {
        List<OrderSpecifier> orders = QueryDslUtil.getAllOrderSpecifiers(pageable);

        JPAQuery<Tuple> query = jpaQueryFactory
                .select(
                        user.id,
                        user.loginId,
                        user.username,
                        user.nickname,
                        user.phoneNumber,
                        user.shippingAddress,
                        user.userRole,
                        user.createdAt,
                        user.updatedAt
                )
                .from(user)
                .where(
                        user.isDeleted.isFalse()
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        log.info("query from jpaQueryFactory: {}", query);
        List<Tuple> results = query.fetch();
        List<UserGetResponseDto> userGetResponseDtoList = new ArrayList<>();
        for (Tuple result: results) {
            userGetResponseDtoList.add(tupleToResponse(result));
        }

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(user.deletedAt.isNull());
        return new PageImpl<>(userGetResponseDtoList, pageable, countQuery.fetchCount());
    }

    private UserGetResponseDto tupleToResponse(Tuple result) {
        return UserGetResponseDto.builder()
                .userId(result.get(0, Long.class))
                .loginId(result.get(1, String.class))
                .password(null)
                .username(result.get(2, String.class))
                .nickname(result.get(3, String.class))
                .phoneNum(result.get(4, String.class))
                .shippingAddress(result.get(5, String.class))
                .userRole(result.get(6, UserRole.class).toString())
                .createdAt(result.get(7, LocalDateTime.class))
                .updatedAt(result.get(8, LocalDateTime.class))
                .build();
    }
}
