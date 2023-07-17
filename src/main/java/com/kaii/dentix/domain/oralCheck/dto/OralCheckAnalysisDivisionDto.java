package com.kaii.dentix.domain.oralCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckAnalysisDivisionDto {

    private OralCheckAnalysisTotalDto upRight; // 우상 영역

    private OralCheckAnalysisTotalDto upLeft; // 좌상 영역

    private OralCheckAnalysisTotalDto downRight; // 우하 영역

    private OralCheckAnalysisTotalDto downLeft; // 좌하 영역


}
