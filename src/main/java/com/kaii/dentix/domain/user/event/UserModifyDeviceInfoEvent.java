package com.kaii.dentix.domain.user.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserModifyDeviceInfoEvent {

    private final Long userId;
    private final HttpServletRequest httpServletRequest;
    private final String userDeviceModel;
    private final String userDeviceManufacturer;
    private final String userOsVersion;
    private final String userDeviceToken;

}
