package com.kaii.dentix.domain.oralCheck.dto.resoponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckAnalysisDivisionDto;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckAnalysisTeethDto;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckAnalysisTotalDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  구강 검진 사진 분석 결과
 */
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckAnalysisResponse {

    private int resultCode; // 결과 코드

    private OralCheckAnalysisTotalDto total; // 전체

    @JsonProperty("tDivision")
    private OralCheckAnalysisDivisionDto tDivision; // 4등분 목록

    private OralCheckAnalysisTeethDto teeth; // 개별 치아 목록

}
