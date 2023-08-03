package com.kaii.dentix.domain.contents.dto;

import lombok.*;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ContentsCategoryDto {

    private int id;

    private String name;

    private String color;

    @Setter
    private int sort;

}
