package com.j1p3ter.userserver.infrastructure.configuration;

import com.j1p3ter.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class BindingResultAop {

    private final Validator validator;

    @Before("execution(* com.j1p3ter.userserver.presentation..*Controller.*(..))")
    public void bindingBefore(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            BindingResult bindingResult = new BeanPropertyBindingResult(arg, arg.getClass().getName());
            validator.validate(arg, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        bindingResult.getFieldError().getDefaultMessage(),
                        log.getName());
            }
        }
    }

}