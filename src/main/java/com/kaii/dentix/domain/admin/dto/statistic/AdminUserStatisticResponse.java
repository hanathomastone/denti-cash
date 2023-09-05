package com.kaii.dentix.domain.admin.dto.statistic;

import com.kaii.dentix.domain.admin.dto.AdminUserSignUpCountDto;
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

    private int oralCheckAverage; // 사용자당 평균 구강검진 횟수

    private OralCheckResultTypeCount oralCheckResultTypeCount; // 구강검진 결과 타입별 횟수

    private int questionnaireAllCount; // 전체 문진표 작성 횟수

    private AllQuestionnaireResultTypeCount allQuestionnaireResultTypeCount; // 문진표 결과 타입별 횟수

}
