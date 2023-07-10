package com.kaii.dentix.global.common.filter;

import com.kaii.dentix.domain.type.DeviceType;
import com.kaii.dentix.domain.userDeviceType.application.UserDeviceTypeService;
import com.kaii.dentix.global.common.error.ErrorResponse;
import com.kaii.dentix.global.common.response.ResponseMessage;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class VersionCheckFilter implements Filter {

    private final UserDeviceTypeService userDeviceTypeService;

    /**
     * 필터 실행 부분 입니다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info(" ::: VersionCheckFilter ::: ");

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        String deviceType = servletRequest.getHeader("deviceType");
        String appVersion = servletRequest.getHeader("appVersion");

        try {
            if (userDeviceTypeService.headerCheck(servletRequest)) {
                if (!userDeviceTypeService.versionCheck(DeviceType.valueOf(deviceType), appVersion)) {
                    ErrorResponse.of(servletResponse, HttpStatus.UPGRADE_REQUIRED, ResponseMessage.UPDATE_REQUIRED_MSG);
                    return;
                }
            }
        } catch (Exception e) {
            ErrorResponse.of(servletResponse, HttpStatus.NOT_ACCEPTABLE, ResponseMessage.REQUIRED_VERSION_INFO_MSG);
            return;
        }

        chain.doFilter(request, response);
    }
}
