package com.j1p3ter.productserver.application.dto.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartAllowRequestDto {
    Long count;
    Long delay;
}