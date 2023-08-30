package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dao.user.UserCustomRepository;
import com.kaii.dentix.domain.admin.dto.AdminStatisticDto;
import com.kaii.dentix.domain.admin.dto.AdminUserSignUpCountDto;
import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStatisticService {

    private final UserCustomRepository userCustomRepository;

    /**
     *  사용자 통계
     */
    @Transactional(readOnly = true)
    public AdminStatisticDto userStatistic(AdminStatisticRequest request){

        // 전체 남녀 가입률
        AdminUserSignUpCountDto userSignUpCount = userCustomRepository.userSignUpCount(request);

        // 평균 구강검진


        // 평균 문진표 유형

    }
}
