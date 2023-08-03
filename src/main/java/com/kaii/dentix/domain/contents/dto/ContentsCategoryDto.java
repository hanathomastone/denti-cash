package com.kaii.dentix.domain.contents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ContentsCategoryDto {

    private int id;

    private String name;

    private String color;

    private int sort;

    // 사용자 맞춤 카테고리가 추가될 경우, sort 값 재정렬을 위해
    public void setSort(int sort){
        this.sort = sort;
    }

}
