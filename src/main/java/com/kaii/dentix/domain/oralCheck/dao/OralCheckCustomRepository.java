package com.kaii.dentix.domain.oralCheck.dao;

import com.kaii.dentix.domain.admin.dto.statistic.OralCheckStatisticDto;
import com.kaii.dentix.domain.admin.dto.statistic.OralCheckUserCount;
import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;

import java.util.List;

public interface OralCheckCustomRepository {

    List<OralCheckStatisticDto> userStateAll(AdminStatisticRequest request);

    List<OralCheckUserCount> oralCheckUserCountAll(AdminStatisticRequest request);

}
