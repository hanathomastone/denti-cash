package com.kaii.dentix.domain.admin.admin.application;

import com.kaii.dentix.domain.admin.admin.dao.AdminRepository;
import com.kaii.dentix.domain.admin.admin.domain.Admin;
import com.kaii.dentix.domain.admin.admin.dto.AdminLoginDto;
import com.kaii.dentix.domain.admin.admin.dto.AdminSignUpDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminLoginRequest;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminSignUpRequest;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.global.common.error.exception.AlreadyDataException;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import com.kaii.dentix.global.common.error.exception.UnauthorizedException;
import com.kaii.dentix.global.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminLoginService {

    private final AdminRepository adminRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    /**
     *  관리자 등록
     */
    @Transactional
    public AdminSignUpDto adminSignUp(AdminSignUpRequest request){

        // 이미 가입된 사용자의 경우
        if (adminRepository.findByAdminNameAndAdminPhoneNumber(request.getAdminName(), request.getAdminPhoneNumber()).isPresent()) throw  new AlreadyDataException("이미 가입한 관리자입니다.");

        // 연락처 중복 확인
        if (adminRepository.findByAdminPhoneNumber(request.getAdminPhoneNumber()).isPresent()) throw  new BadRequestApiException("이미 사용중인 번호에요. 번호를 다시 확인해 주세요.");

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
     *  관리자 로그인
     */
    @Transactional
    public AdminLoginDto adminLogin(AdminLoginRequest request){
        Admin admin = adminRepository.findByAdminLoginIdentifier(request.getAdminLoginIdentifier()).orElseThrow(() -> new NotFoundDataException("입력하신 정보가 일치하지 않습니다. 다시 확인해주세요."));

        YnType isFirstLogin = admin.getAdminLastLoginDate() == null ? YnType.Y : YnType.N;

        // 처음 로그인 시도인 경우
        if (admin.getAdminPassword() == null || admin.getAdminPassword().isEmpty()){
            isFirstLogin = YnType.Y; // 비밀번호 초기화를 위해
            admin.modifyAdminPassword(passwordEncoder, SecurityUtil.defaultPassword);
        }

        if (!passwordEncoder.matches(request.getAdminPassword(), admin.getAdminPassword())){
            throw new UnauthorizedException("입력하신 정보가 일치하지 않습니다. 다시 확인해주세요.");
        }

        String accessToken = jwtTokenUtil.createToken(admin, TokenType.AccessToken);
        String refreshToken = jwtTokenUtil.createToken(admin, TokenType.RefreshToken);

        admin.updateAdminLogin(refreshToken);

        return AdminLoginDto.builder()
                .isFirstLogin(isFirstLogin)
                .adminId(admin.getAdminId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .adminName(admin.getAdminName())
                .isSuper(admin.getAdminIsSuper())
                .build();

    }

}
