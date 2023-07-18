package com.kaii.dentix.domain.oralCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckAreaPlaqueRatioDto {

    private Integer area; // 면적 Pixel 수

    private Integer plaque; // 플라그 Pixel 수

    private Float ratio; // 비율

}
