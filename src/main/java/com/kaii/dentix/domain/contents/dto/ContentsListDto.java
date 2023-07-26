package com.kaii.dentix.domain.contents.dto;

import com.kaii.dentix.domain.contents.domain.ContentsList;
import com.kaii.dentix.domain.type.ContentsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ContentsListDto {

    private Long contentsId;

    private String contentsTitle;

    private int contentsSort;

    private ContentsType contentsType;

    private String contentsTitleColor;

    private String contentsThumbnail;

    private String contentsPath;

    private List<ContentsList> contentsLists;

}
