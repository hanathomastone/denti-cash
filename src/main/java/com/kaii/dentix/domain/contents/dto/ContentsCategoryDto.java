package com.kaii.dentix.domain.contents.dto;

import lombok.*;

@Getter @Builder @Setter
@AllArgsConstructor @NoArgsConstructor
public class ContentsCategoryDto {

    private int id;

    private String name;

    private String color;

    private int sort;

}
