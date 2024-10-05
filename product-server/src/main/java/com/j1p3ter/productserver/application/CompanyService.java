package com.j1p3ter.productserver.application;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.productserver.application.dto.company.CompanyCreateRequestDto;
import com.j1p3ter.productserver.application.dto.company.CompanyResponseDto;
import com.j1p3ter.productserver.domain.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j(topic = "Company Service")
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyResponseDto createCompany(CompanyCreateRequestDto requestDto) {
        // userID 검증 로직 추가 필요

        try{
            return CompanyResponseDto.fromCompany(companyRepository.save(requestDto.toEntity()));
        }catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Company 생성에 실패했습니다." ,e.getMessage());
        }

    }
}
