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

    private Long contentsId;

    private String contentsTitle;

    private int contentsSort;

    private ContentsType contentsType;

    private String contentsTypeColor;

    private String contentsThumbnail;

    private String contentsPath;

    private List<ContentsCategoryIdsDto> categoryIds;

}
