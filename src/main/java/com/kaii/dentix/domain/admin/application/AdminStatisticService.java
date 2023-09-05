package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dao.user.UserCustomRepository;
import com.kaii.dentix.domain.admin.dto.*;
import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;
import com.kaii.dentix.domain.admin.dto.statistic.*;
import com.kaii.dentix.domain.oralCheck.application.OralCheckService;
import com.kaii.dentix.domain.oralCheck.dao.OralCheckCustomRepository;
import com.kaii.dentix.domain.admin.dto.statistic.OralCheckResultTypeCount;
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
        List<OralCheckStatisticDto> userOralCheckList = oralCheckCustomRepository.userOralCheckList(request); // 구강검진을 한 모든 사용자 리스트

        List<OralCheckUserCount> allUserOralCheckCount = oralCheckCustomRepository.allUserOralCheckCount(request);  // 구강검진을 한 총 사용자 수
        int userCount = allUserOralCheckCount.size();

        int allOralCheckCount = 0; // 전체 구강검진 횟수
        int oralCheckAverage = 0; // 사용자 당 평균 구강검진 횟수

        OralCheckResultTypeCount oralCheckResultTypeCount = new OralCheckResultTypeCount(); // 구강검진 결과 타입별 횟수

        if (userCount > 0) {
            int countHealthy = 0;
            int countGood = 0;
            int countAttention = 0;
            int countDanger = 0;

            for (OralCheckStatisticDto oralCheckStatisticDto : userOralCheckList){
                allOralCheckCount += oralCheckStatisticDto.getOralCheckCount();

                switch (oralCheckStatisticDto.getOralCheckResultTotalType()) { // 구강검진 결과 타입별 횟수 count
                    case HEALTHY -> countHealthy ++;
                    case GOOD -> countGood ++;
                    case ATTENTION -> countAttention ++;
                    case DANGER -> countDanger ++;
                }
            }

            oralCheckResultTypeCount = OralCheckResultTypeCount.builder()
                    .countHealthy(countHealthy)
                    .countGood(countGood)
                    .countAttention(countAttention)
                    .countDanger(countDanger)
                    .build();

            oralCheckAverage = Math.round((float) allOralCheckCount / userCount);
        }

        OralCheckResultTotalType averageState = oralCheckService.getState(oralCheckResultTypeCount); // 전체 평균 구강 상태

        // 통계 3. 평균 문진표 유형
        List<QuestionnaireStatisticDto> questionnaireList = questionnaireCustomRepository.questionnaireList(request); // 모든 문진표 리스트

        List<AllQuestionnaireCount> questionnaireCountList = questionnaireCustomRepository.questionnaireCountList(request);
        int questionnaireAllCount = 0; // 전체 문진표 작성 횟수

        AllQuestionnaireResultTypeCount allQuestionnaireResultTypeCount = new AllQuestionnaireResultTypeCount(); // 모든 문진표 결과 유형

        for (AllQuestionnaireCount allQuestionnaireCount : questionnaireCountList) { // 전체 문진표 작성 횟수 count
            questionnaireAllCount += allQuestionnaireCount.getAllQuestionnaireCount();
        }

        if (questionnaireAllCount > 0){
            int countA = 0;
            int countB = 0;
            int countC = 0;
            int countD = 0;
            int countE = 0;
            int countF = 0;
            int countG = 0;
            int countH = 0;
            int countI = 0;
            int countJ = 0;
            int countK = 0;

            for (QuestionnaireStatisticDto questionnaireStatisticDto : questionnaireList){
                switch (questionnaireStatisticDto.getQuestionnaireType()) { // 문진표 결과 타입별 횟수 count
                    case "A" -> countA ++;
                    case "B" -> countB ++;
                    case "C" -> countC ++;
                    case "D" -> countD ++;
                    case "E" -> countE ++;
                    case "F" -> countF ++;
                    case "G" -> countG ++;
                    case "H" -> countH ++;
                    case "I" -> countI ++;
                    case "J" -> countJ ++;
                    case "K" -> countK ++;
                }
            }

            allQuestionnaireResultTypeCount = AllQuestionnaireResultTypeCount.builder()
                    .countA(countA)
                    .countB(countB)
                    .countC(countC)
                    .countD(countD)
                    .countE(countE)
                    .countF(countF)
                    .countG(countG)
                    .countH(countH)
                    .countI(countI)
                    .countJ(countJ)
                    .countK(countK)
                    .build();
        }

        return AdminUserStatisticResponse.builder()
                .userSignUpCount(userSignUpCount)
                .averageState(averageState)
                .oralCheckCount(allOralCheckCount)
                .oralCheckAverage(oralCheckAverage)
                .oralCheckResultTypeCount(oralCheckResultTypeCount)
                .questionnaireAllCount(questionnaireAllCount)
                .allQuestionnaireResultTypeCount(allQuestionnaireResultTypeCount)
                .build();
    }
}
