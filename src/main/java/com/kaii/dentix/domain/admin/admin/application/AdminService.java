package com.kaii.dentix.domain.admin.admin.application;

import com.kaii.dentix.domain.admin.admin.dao.AdminRepository;
import com.kaii.dentix.domain.admin.admin.domain.Admin;
import com.kaii.dentix.domain.admin.admin.dto.AdminSignUpDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminModifyPasswordRequest;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminSignUpRequest;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.type.UserRole;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.global.common.error.exception.AlreadyDataException;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import com.kaii.dentix.global.common.error.exception.UnauthorizedException;
import com.kaii.dentix.global.common.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 토큰에서 Admin 추출
     */
    public Admin getTokenAdmin(HttpServletRequest servletRequest) {
        String token = jwtTokenUtil.getAccessToken(servletRequest);

        UserRole roles = jwtTokenUtil.getRoles(token, TokenType.AccessToken);
        if (!roles.equals(UserRole.ROLE_ADMIN)) throw new UnauthorizedException();

        Long adminId = jwtTokenUtil.getUserId(token, TokenType.AccessToken);
        return adminRepository.findById(adminId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 관리자입니다."));
    }

    /**
     *  관리자 등록
     */
    @Transactional
    public AdminSignUpDto adminSignUp(AdminSignUpRequest request){

        Optional<Admin> existAdmin = adminRepository.findByAdminPhoneNumber(request.getAdminPhoneNumber());

        if (existAdmin.isPresent()){
            // 이미 가입된 사용자의 경우
            if (existAdmin.get().getAdminName().equals(request.getAdminName())) throw new AlreadyDataException("이미 가입한 관리자입니다.");

            // 연락처 중복 확인
            throw new BadRequestApiException("이미 사용중인 번호에요. 번호를 다시 확인해 주세요.");
        }

        // 아이디 중복 확인
        if (adminRepository.findByAdminLoginIdentifier(request.getAdminLoginIdentifier()).isPresent()) throw new AlreadyDataException("이미 존재하는 아이디입니다.");

        Admin admin = Admin.builder()
                .adminName(request.getAdminName())
                .adminLoginIdentifier(request.getAdminLoginIdentifier())
                .adminPhoneNumber(request.getAdminPhoneNumber())
                .adminIsSuper(YnType.N)
                .build();

        adminRepository.save(admin);

        return AdminSignUpDto.builder()
                .adminId(admin.getAdminId())
                .adminPassword(SecurityUtil.defaultPassword)
                .build();

    }

    /**
     *  관리자 비밀번호 변경
     */
    @Transactional
    public void adminModifyPassword(HttpServletRequest httpServletRequest, AdminModifyPasswordRequest request){
        Admin admin = this.getTokenAdmin(httpServletRequest);

        admin.modifyAdminPassword(passwordEncoder, request.getAdminPassword());
    }

    /**
     *  관리자 삭제
     */
    @Transactional
    public void adminDelete(Long adminId){
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 관리자입니다."));
        admin.deleteAdmin();
    }

}
