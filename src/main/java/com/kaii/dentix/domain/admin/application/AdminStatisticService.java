package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dao.user.UserCustomRepository;
import com.kaii.dentix.domain.admin.dto.*;
import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;
import com.kaii.dentix.domain.admin.dto.statistic.AdminUserStatisticResponse;
import com.kaii.dentix.domain.admin.dto.statistic.OralCheckStatisticDto;
import com.kaii.dentix.domain.admin.dto.statistic.OralCheckUserCount;
import com.kaii.dentix.domain.admin.dto.statistic.QuestionnaireStatisticDto;
import com.kaii.dentix.domain.oralCheck.application.OralCheckService;
import com.kaii.dentix.domain.oralCheck.dao.OralCheckCustomRepository;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckResultCount;
import com.kaii.dentix.domain.questionnaire.dao.QuestionnaireCustomRepository;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatisticService {

    private final UserCustomRepository userCustomRepository;

    private final OralCheckCustomRepository oralCheckCustomRepository;

    private final OralCheckService oralCheckService;

    private final QuestionnaireCustomRepository questionnaireCustomRepository;

    /**
     *  사용자 통계
     */
    @Transactional(readOnly = true)
    public AdminUserStatisticResponse userStatistic(AdminStatisticRequest request){

        // 통계 1. 전체 남녀 가입률
        AdminUserSignUpCountDto userSignUpCount = userCustomRepository.userSignUpCount(request);

        // 통계 2. 평균 구강검진
        List<OralCheckStatisticDto> oralCheckUserAll = oralCheckCustomRepository.userStateAll(request); // 구강검진을 한 모든 사용자 리스트

        List<OralCheckUserCount> oralCheckUserCount = oralCheckCustomRepository.oralCheckUserCountAll(request);  // 구강검진을 한 총 사용자 수
        int userCount = oralCheckUserCount.size();

        int oralCheckCount = 0; // 전체 구강검진 횟수
        int oralCheckAverage = 0; // 사용자 당 평균 구강검진 횟수

        OralCheckResultCount oralCheckResultCount = new OralCheckResultCount();

        if (userCount > 0) {
            int countHealthy = 0;
            int countGood = 0;
            int countAttention = 0;
            int countDanger = 0;

            for (OralCheckStatisticDto adminUserStatisticDto : oralCheckUserAll){
                oralCheckCount += adminUserStatisticDto.getOralCheckCount(); // 전체 구강검진 횟수

                switch (adminUserStatisticDto.getOralCheckResultTotalType()) { // 구강검진 결과 타입별 횟수
                    case "HEALTHY" -> countHealthy ++;
                    case "GOOD" -> countGood ++;
                    case "ATTENTION" -> countAttention ++;
                    case "DANGER" -> countDanger ++;
                }
            }

            oralCheckResultCount = OralCheckResultCount.builder()
                    .countHealthy(countHealthy)
                    .countGood(countGood)
                    .countAttention(countAttention)
                    .countDanger(countDanger)
                    .build();

            oralCheckAverage = Math.round((float) oralCheckCount / userCount); // 사용자 당 평균 구강검진 횟수
        }

        OralCheckResultTotalType averageState = oralCheckService.getState(oralCheckResultCount); // 전체 평균 구강 상태

        // 통계 3. 평균 문진표 유형
        List<QuestionnaireStatisticDto> questionnaireAll = questionnaireCustomRepository.questionnaireStateAll(request);

        return AdminUserStatisticResponse.builder()
                .userSignUpCount(userSignUpCount)
                .averageState(averageState)
                .oralCheckCount(oralCheckCount)
                .oralCheckAverage(oralCheckAverage)
                .oralCheckResultCount(oralCheckResultCount)
                .build();
    }
}
