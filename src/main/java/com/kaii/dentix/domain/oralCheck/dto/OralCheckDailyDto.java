package com.kaii.dentix.domain.oralCheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.type.OralDateStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 구강검진 일별 리스트
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OralCheckDailyDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date date;

    private OralDateStatusType status;
    private boolean questionnaire;

    @Builder.Default
    private List<OralCheckListDto> detailList = new ArrayList<>();
}
