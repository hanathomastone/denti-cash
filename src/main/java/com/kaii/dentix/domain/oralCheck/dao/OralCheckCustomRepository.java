package com.kaii.dentix.domain.oralCheck.dao;

import com.kaii.dentix.domain.admin.dto.statistic.OralCheckResultTypeCount;
import com.kaii.dentix.domain.admin.dto.statistic.OralCheckUserCount;
import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;

import java.util.List;

public interface OralCheckCustomRepository {

    OralCheckResultTypeCount userOralCheckList(AdminStatisticRequest request);

    List<OralCheckUserCount> allUserOralCheckCount(AdminStatisticRequest request);

}
