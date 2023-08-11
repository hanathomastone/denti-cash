package com.kaii.dentix.domain.admin.admin.application;

import com.kaii.dentix.domain.admin.admin.dao.AdminRepository;
import com.kaii.dentix.domain.admin.admin.domain.Admin;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminModifyPasswordRequest;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.type.UserRole;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import com.kaii.dentix.global.common.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     *  관리자 비밀번호 변경
     */
    @Transactional
    public void adminModifyPassword(HttpServletRequest httpServletRequest, AdminModifyPasswordRequest request){
        Admin admin = this.getTokenAdmin(httpServletRequest);

        admin.modifyAdminPassword(passwordEncoder, request.getAdminPassword());
    }

}
