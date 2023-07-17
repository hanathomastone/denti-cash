package com.kaii.dentix.domain.oralCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckAnalysisTotalDto {

    private OralCheckAreaPlaqueRatioDto group; // 그룹

    private OralCheckAreaPlaqueRatioDto interproximal; // 치간

    private OralCheckAreaPlaqueRatioDto cervical; // 치경

    private OralCheckAreaPlaqueRatioDto labial; // 순면


}
