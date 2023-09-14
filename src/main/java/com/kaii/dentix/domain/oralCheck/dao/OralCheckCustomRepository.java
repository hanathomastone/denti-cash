package com.kaii.dentix.domain.oralCheck.dao;

import com.kaii.dentix.domain.admin.dto.statistic.OralCheckResultTypeCount;
import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;

public interface OralCheckCustomRepository {

    OralCheckResultTypeCount userOralCheckList(AdminStatisticRequest request);

    Integer allUserOralCheckCount(AdminStatisticRequest request);

}
