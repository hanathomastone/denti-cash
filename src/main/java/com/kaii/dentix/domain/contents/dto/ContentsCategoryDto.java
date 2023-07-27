package com.kaii.dentix.domain.contents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ContentsCategoryDto {

    private Long contentsCategoryId;

    private String contentsCategoryName;

    private String contentsCategoryColor;

    private int contentsCategorySort;

}
