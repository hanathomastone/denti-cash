package com.kaii.dentix.domain.oralCheck.dto.resoponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckPhotoResponse {

    private int rt; // 결과 코드

    private String rtMsg; // 결과 메시지

    private Long oralCheckId; // 구강 검진 고유 번호

}
