package com.kaii.dentix.domain.blockChain.mission.dao;




import com.kaii.dentix.domain.blockChain.mission.domain.Mission;

import java.time.LocalDate;
import java.util.List;

public interface MissionRepositoryCustom {
    List<Mission> searchMissions(String keyword, LocalDate startDate, LocalDate endDate, Boolean active);
}