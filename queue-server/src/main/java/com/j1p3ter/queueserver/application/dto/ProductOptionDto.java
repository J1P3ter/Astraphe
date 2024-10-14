package com.j1p3ter.queueserver.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDto {
    private String optionName;
    private String optionValue;
    private Integer optionPrice;
}