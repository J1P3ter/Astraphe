package com.j1p3ter.queueserver.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankResponseDto {
    private Long userId;
    private Long rank;
}
