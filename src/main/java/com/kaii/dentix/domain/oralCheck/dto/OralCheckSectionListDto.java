package com.kaii.dentix.domain.oralCheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingDto;
import com.kaii.dentix.domain.type.OralSectionType;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 구강검진 리스트
 */
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckSectionListDto {

    @Setter
    private int sort;

    private OralSectionType sectionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date date;

    private Long timeInterval;

    @Builder.Default
    private List<ToothBrushingDto> toothBrushingList = new ArrayList<>();
}
