package com.j1p3ter.queueserver.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class StartAllowRequestDto {
    Long count;
    Long delay;
}
