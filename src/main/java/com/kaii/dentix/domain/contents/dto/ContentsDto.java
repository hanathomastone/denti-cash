package com.kaii.dentix.domain.contents.dto;

import com.kaii.dentix.domain.type.ContentsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ContentsDto {

    private int id;

    private String title;

    private int sort;

    private ContentsType type;

    private String typeColor;

    private String thumbnail;

    private String videoURL;

    private List<Integer> categoryIds;

}
