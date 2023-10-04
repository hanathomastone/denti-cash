package com.kaii.dentix.domain.oralCheck.dto.resoponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckAnalysisDivisionDto;
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

    @JsonProperty("status_code")
    private int statusCode; // 결과 코드

    @JsonProperty("status_msg")
    private String statusMsg; // 결과 메시지

    @JsonProperty("plaque_stats")
    private OralCheckAnalysisDivisionDto plaqueStats; // 4등분 목록

}
