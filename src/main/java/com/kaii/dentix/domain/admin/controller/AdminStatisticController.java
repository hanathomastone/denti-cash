package com.kaii.dentix.domain.admin.controller;

import com.kaii.dentix.domain.admin.application.AdminStatisticService;
import com.kaii.dentix.domain.admin.dto.AdminStatisticDto;
import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;
import com.kaii.dentix.global.common.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/statistic")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminStatisticController {

    private final AdminStatisticService adminStatisticService;

    /**
     *  사용자 통계
     */
    @GetMapping(name = "사용자 통계")
    public DataResponse<AdminStatisticDto> userStatistic(AdminStatisticRequest request){
        DataResponse<AdminStatisticDto> response = new DataResponse<>(adminStatisticService.userStatistic(request));
        return response;
    }
}
