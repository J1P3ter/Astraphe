package com.j1p3ter.gateway.infrastructure.exception;

import com.j1p3ter.gateway.infrastructure.config.InvalidQueueTokenExceptionCase;
import com.j1p3ter.gateway.infrastructure.config.InvalidTokenExceptionCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class InvalidQueueTokenException extends RuntimeException{

    private final InvalidQueueTokenExceptionCase QueueExceptionCase;

    public InvalidQueueTokenException(InvalidQueueTokenExceptionCase QueueExceptionCase) {
        super(QueueExceptionCase.getMessage());
        this.QueueExceptionCase = QueueExceptionCase;
    }
}