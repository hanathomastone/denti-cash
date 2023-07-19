package com.kaii.dentix.domain.oralCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  구강 검진 사진 결과
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OralCheckPhotoDto {

    private Long oralCheckId; // 구강 검진 고유 번호
}
