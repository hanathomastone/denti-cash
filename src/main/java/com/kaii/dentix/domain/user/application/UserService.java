package com.kaii.dentix.domain.user.application;

import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.type.DeviceType;
import com.kaii.dentix.domain.type.UserRole;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.domain.user.dto.UserLoginDto;
import com.kaii.dentix.domain.user.dto.request.UserAutoLoginRequest;
import com.kaii.dentix.domain.user.dto.request.UserInfoModifyPasswordRequest;
import com.kaii.dentix.domain.user.dto.request.UserPasswordVerifyRequest;
import com.kaii.dentix.domain.user.event.UserModifyDeviceInfoEvent;
import com.kaii.dentix.domain.userDeviceType.dao.UserDeviceTypeRepository;
import com.kaii.dentix.domain.userDeviceType.domain.UserDeviceType;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import com.kaii.dentix.global.common.error.exception.RequiredVersionInfoException;
import com.kaii.dentix.global.common.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final ApplicationEventPublisher publisher;

    private final UserDeviceTypeRepository userDeviceTypeRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    /**
     * 토큰에서 User 추출
     */
    public User getTokenUser(HttpServletRequest servletRequest) {

        String token = jwtTokenUtil.getAccessToken(servletRequest);

        UserRole roles = jwtTokenUtil.getRoles(token, TokenType.AccessToken);
        if (!roles.equals(UserRole.ROLE_USER)) throw new UnauthorizedException("권한이 없는 사용자입니다.");

        Long userId = jwtTokenUtil.getUserId(token, TokenType.AccessToken);
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 사용자입니다."));

    }

    /**
     *  사용자 앱 정보 업데이트
     */
    @EventListener
    public void userModifyDeviceInfo(UserModifyDeviceInfoEvent event){

        HttpServletRequest servletRequest = event.getHttpServletRequest();

        UserDeviceType userDeviceType;
        String appVersion;

        try {
            DeviceType deviceType = DeviceType.valueOf(servletRequest.getHeader("deviceType"));
            userDeviceType = userDeviceTypeRepository.findByUserDeviceType(deviceType).orElseThrow(() -> new NotFoundDataException("deviceType"));
            appVersion = servletRequest.getHeader("appVersion");
        } catch (Exception e) {
            throw new RequiredVersionInfoException();
        }

        User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new NotFoundDataException("User"));

        user.modifyDeviceInfo(
                userDeviceType.getUserDeviceTypeId(),
                appVersion,
                event.getUserDeviceModel(),
                event.getUserDeviceManufacturer(),
                event.getUserOsVersion(),
                event.getUserDeviceToken()
        );

    }


    /**
     *  사용자 자동 로그인
     */
    @Transactional
    public UserLoginDto userAutoLogin(HttpServletRequest httpServletRequest, UserAutoLoginRequest userAutoLoginRequest){

        User user = this.getTokenUser(httpServletRequest);

        String accessToken = jwtTokenUtil.createToken(user, TokenType.AccessToken);
        String refreshToken = jwtTokenUtil.createToken(user, TokenType.RefreshToken);

        user.updateLogin(refreshToken);

        publisher.publishEvent(new UserModifyDeviceInfoEvent(
                user.getUserId(),
                httpServletRequest,
                userAutoLoginRequest.getUserDeviceModel(),
                userAutoLoginRequest.getUserDeviceManufacturer(),
                userAutoLoginRequest.getUserOsVersion(),
                userAutoLoginRequest.getUserDeviceToken()
        ));

        return UserLoginDto.builder()
                .userId(user.getUserId())
                .userLoginId(user.getUserLoginId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    /**
     *  사용자 비밀번호 확인
     */
    @Transactional
    public void userPasswordVerify(HttpServletRequest httpServletRequest, UserPasswordVerifyRequest request){

        User user = this.getTokenUser(httpServletRequest);

        if (!passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())){
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

    }

    /**
     *  사용자 보안정보수정 - 비밀번호 변경
     */
    @Transactional
    public void userModifyPassword(HttpServletRequest httpServletRequest, UserInfoModifyPasswordRequest request){

        User user = this.getTokenUser(httpServletRequest);

        user.modifyUserPassword(passwordEncoder, request.getUserPassword());

    }

}
