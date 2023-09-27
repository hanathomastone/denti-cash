package com.kaii.dentix.domain.oralCheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.questionnaire.dto.OralStatusTypeDto;
import com.kaii.dentix.domain.type.OralSectionType;
import com.kaii.dentix.domain.type.oral.OralCheckResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 구강검진 리스트
 */
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckListDto {

    private OralSectionType sectionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date date;

    private long identifier;
    private OralCheckResultType oralCheckResultTotalType;
    private Integer toothBrushingCount;

    @Builder.Default
    private List<OralStatusTypeDto> oralStatusList = new ArrayList<>();
}
