package com.kaii.dentix.domain.admin.admin.application;

import com.kaii.dentix.domain.admin.admin.dao.AdminRepository;
import com.kaii.dentix.domain.admin.admin.domain.Admin;
import com.kaii.dentix.domain.admin.admin.dto.AdminSignUpDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminSignUpRequest;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.global.common.error.exception.AlreadyDataException;
import com.kaii.dentix.global.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    /**
     *  관리자 등록
     */
    @Transactional
    public AdminSignUpDto adminSignUp(AdminSignUpRequest request){

        // 아이디 중복 확인
        if (adminRepository.findByAdminLoginIdentifier(request.getAdminLoginIdentifier()).isPresent()) throw new AlreadyDataException("이미 존재하는 아이디입니다.");

        // 이름, 연락처 중복 확인
        if (adminRepository.findByAdminNameAndAdminPhoneNumberAndAdminIsSuper(request.getAdminName(), request.getAdminPhoneNumber(), YnType.N).isPresent())
            throw new AlreadyDataException("이미 존재하는 관리자 정보입니다.");

        Admin admin = Admin.builder()
                .adminName(request.getAdminName())
                .adminLoginIdentifier(request.getAdminLoginIdentifier())
                .adminPhoneNumber(request.getAdminPhoneNumber())
                .adminIsSuper(YnType.N)
                .build();

        adminRepository.save(admin);

        return AdminSignUpDto.builder()
                .adminId(admin.getAdminId())
                .adminName(admin.getAdminName())
                .adminPassword(SecurityUtil.defaultPassword)
                .build();

    }

}
