package com.kaii.dentix.domain.contents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ContentsCategoryListDto {

    List<ContentsCategories> categories;

    List<ContentsListDto> contents;

}