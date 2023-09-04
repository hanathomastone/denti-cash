package com.kaii.dentix.domain.admin.dto.statistic;

import com.kaii.dentix.domain.admin.dto.AdminUserSignUpCountDto;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckResultCount;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminUserStatisticResponse {

    private AdminUserSignUpCountDto userSignUpCount; // 전체 남녀 가입률

    private OralCheckResultTotalType averageState; // 평균 구강 상태

    private int oralCheckCount; // 전체 구강검진 횟수

    private int oralCheckAverage; // 사용자 평균 구강검진 횟수

    private OralCheckResultCount oralCheckResultCount; // 구강검진 결과 타입별 횟수

    private String questionnaireType; // 가장 많은 문진표 결과 유형

    private int questionnaireCount; // 전체 문진표 작성 횟수

    private int questionnaireTypeCount; // 가장 많은 문진표 결과 유형의 총 횟수

    // 문진표 타입 순위 3위

    private QuestionnaireResultCount questionnaireResultCount; // 문진표 결과 타입별 횟수

}
