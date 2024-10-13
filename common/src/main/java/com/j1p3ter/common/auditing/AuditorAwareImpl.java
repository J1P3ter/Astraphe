package com.j1p3ter.common.auditing;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    // ThreadLocal을 사용해 비동기 환경에서 사용자 ID를 설정
    private static final ThreadLocal<String> auditor = new ThreadLocal<>();

    @Override
    public Optional<Long> getCurrentAuditor() {
        String updatedBy;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            updatedBy = request.getHeader("X-USER-ID");

            if (updatedBy == null) {
                updatedBy = "-1"; // 기본 사용자 ID 설정
            }
        } else {
            // HTTP 요청이 없는 경우 (비동기 작업 등), ThreadLocal에서 사용자 ID를 가져옴
            updatedBy = auditor.get();
            if (updatedBy == null) {
                updatedBy = "-1"; // 기본 사용자 ID 설정
            }
        }

        return Optional.of(Long.parseLong(updatedBy));
    }

    public static void setAuditor(String userId) {
        auditor.set(userId);
    }

    public static void clear() {
        auditor.remove();
    }

}