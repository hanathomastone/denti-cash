package com.kaii.dentix.domain.userDeviceType.application;

import com.kaii.dentix.domain.type.DeviceType;
import com.kaii.dentix.domain.userDeviceType.dao.UserDeviceTypeRepository;
import com.kaii.dentix.domain.userDeviceType.domain.UserDeviceType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeviceTypeService {

    private final UserDeviceTypeRepository userDeviceTypeRepository;

    /**
     * UserRole 확인
     */
    public Boolean headerCheck(HttpServletRequest request) {
        String deviceType = request.getHeader("deviceType");
        String appVersion = request.getHeader("appVersion");

        return deviceType != null || appVersion != null;
    }

    /**
     * 버전 확인
     */
    public Boolean versionCheck(DeviceType deviceType, String appVersion) {

        UserDeviceType userDeviceType = userDeviceTypeRepository.findByUserDeviceType(deviceType).orElse(null);

        if (userDeviceType == null) return false;

        switch (deviceType) {
            case iOS:
                String[] appVersionSplit = appVersion.split("\\.");
                if (appVersionSplit.length != 3) return false; // build number로 넘어오는 경우 업데이트 처리

                String[] minimumVersionSplit = userDeviceType.getUserDeviceTypeMinVersion().split("\\.");

                int appVersion1 = Integer.parseInt(appVersionSplit[0]);
                int minimumVersion1 = Integer.parseInt(minimumVersionSplit[0]);
                if (appVersion1 != minimumVersion1) return appVersion1 > minimumVersion1;

                int appVersion2 = Integer.parseInt(appVersionSplit[1]);
                int minimumVersion2 = Integer.parseInt(minimumVersionSplit[1]);
                if (appVersion2 != minimumVersion2) return appVersion2 > minimumVersion2;

                int appVersion3 = Integer.parseInt(appVersionSplit[2]);
                int minimumVersion3 = Integer.parseInt(minimumVersionSplit[2]);
                return appVersion3 >= minimumVersion3;
            default:
                return false;
        }
    }

}
