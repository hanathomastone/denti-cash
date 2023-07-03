package com.kaii.dentix.domain.user.application;

import com.kaii.dentix.domain.findPwdQuestion.dao.FindPwdQuestionRepository;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.patient.dao.PatientRepository;
import com.kaii.dentix.domain.patient.domain.Patient;
import com.kaii.dentix.domain.serviceAgreement.application.ServiceAgreementService;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementDto;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.domain.user.dto.UserLoginDto;
import com.kaii.dentix.domain.user.dto.UserSignUpDto;
import com.kaii.dentix.domain.user.dto.UserVerifyDto;
import com.kaii.dentix.domain.user.dto.request.UserFindPasswordRequest;
import com.kaii.dentix.domain.user.dto.request.UserLoginRequest;
import com.kaii.dentix.domain.user.dto.request.UserSignUpRequest;
import com.kaii.dentix.domain.user.dto.request.UserVerifyRequest;
import com.kaii.dentix.domain.user.event.UserModifyDeviceInfoEvent;
import com.kaii.dentix.domain.userServiceAgreement.dao.UserServiceAgreementRepository;
import com.kaii.dentix.domain.userServiceAgreement.domain.UserServiceAgreement;
import com.kaii.dentix.domain.userServiceAgreement.dto.request.UserServiceAgreementRequest;
import com.kaii.dentix.global.common.error.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final PatientRepository patientRepository;

    private final UserRepository userRepository;

    private final ServiceAgreementService serviceAgreementService;

    private final UserServiceAgreementRepository userServiceAgreementRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final FindPwdQuestionRepository findPwdQuestionRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher publisher;


    /**
     *  사용자 회원 확인
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVerifyDto userVerify(UserVerifyRequest request){

        Patient patient = patientRepository.findByPatientPhoneNumberAndPatientName(request.getPatientPhoneNumber(), request.getPatientName())
                .orElseThrow(() -> new NotFoundDataException("존재하지 않는 회원입니다."));

        if (userRepository.findByPatientId(patient.getPatientId()).isPresent()) throw new AlreadyDataException("이미 가입한 사용자입니다.");

        // 서비스 이용 동의
        List<ServiceAgreementDto> serviceAgreementList = serviceAgreementService.serviceAgreementList();
        if (serviceAgreementList.size() != request.getUserServiceAgreementRequest().size())
            throw new ValidationException("서비스 동의 개수 불일치");

        serviceAgreementList.forEach(serviceAgreementDTO -> {
            UserServiceAgreementRequest userServiceAgreementRequest = request.getUserServiceAgreementRequest().stream()
                    .filter(userServiceAgreementDTO -> serviceAgreementDTO.getServiceAgreeId().equals(userServiceAgreementDTO.getUserServiceAgreeId()))
                    .findAny().orElseThrow(() -> new ValidationException("동의 항목 누락"));

            // 필수 동의 확인
            if (serviceAgreementDTO.getIsServiceAgreeRequired().equals(YnType.Y) && !userServiceAgreementRequest.getIsUserServiceAgree().equals(YnType.Y)) {
                throw new BadRequestApiException(serviceAgreementDTO.getServiceAgreeName() + " : 필수 동의 항목입니다.");
            }

        });

        return UserVerifyDto.builder().patientId(patient.getPatientId()).build();
    }


    /**
     *  사용자 회원가입
     */
    @Transactional
    public UserSignUpDto userSignUp(HttpServletRequest httpServletRequest, UserSignUpRequest request){

        User user = new User();

        patientRepository.findById(request.getPatientId()).orElseThrow(() -> new NotFoundDataException("존재하지 않는 회원입니다."));

        if (userRepository.findByPatientId(request.getPatientId()).isPresent()) throw new AlreadyDataException("이미 가입한 사용자입니다.");

        this.loginIdCheck(request.getUserLoginId());

        if (!findPwdQuestionRepository.findById(request.getFindPwdQuestionId()).isPresent()) throw new NotFoundDataException("존재하지 않는 질문입니다.");

        user = userRepository.save(user.builder()
            .userLoginId(request.getUserLoginId())
            .userName(request.getUserName())
            .userGender(request.getUserGender())
            .userBirth(request.getUserBirth())
            .userPassword(passwordEncoder.encode(request.getUserPassword()))
            .findPwdQuestionId(request.getFindPwdQuestionId())
            .findPwdAnswer(request.getFindPwdAnswer())
            .patientId(request.getPatientId())
        .build());

        Long userId = user.getUserId();

        String accessToken = jwtTokenUtil.createToken(user, TokenType.AccessToken);
        String refreshToken = jwtTokenUtil.createToken(user, TokenType.RefreshToken);

        user.updateLogin(refreshToken);

        // 서비스 이용 동의
        List<ServiceAgreementDto> serviceAgreementList = serviceAgreementService.serviceAgreementList();
        if (serviceAgreementList.size() != request.getUserServiceAgreementRequest().size())
            throw new ValidationException("서비스 동의 개수 불일치");

        serviceAgreementList.forEach(serviceAgreementDTO -> {
            UserServiceAgreementRequest userServiceAgreementRequest = request.getUserServiceAgreementRequest().stream()
                    .filter(userServiceAgreementDTO -> serviceAgreementDTO.getServiceAgreeId().equals(userServiceAgreementDTO.getUserServiceAgreeId()))
                    .findAny().orElseThrow(() -> new ValidationException("동의 항목 누락"));

            Date now = new Date();

            // 필수 동의 확인
            if (serviceAgreementDTO.getIsServiceAgreeRequired().equals(YnType.Y) && !userServiceAgreementRequest.getIsUserServiceAgree().equals(YnType.Y)) {
                throw new BadRequestApiException(serviceAgreementDTO.getServiceAgreeName() + " : 필수 동의 항목입니다.");
            }

            userServiceAgreementRepository.save(UserServiceAgreement.builder()
                    .userId(userId)
                    .serviceAgreeId(serviceAgreementDTO.getServiceAgreeId())
                    .isUserServiceAgree(userServiceAgreementRequest.getIsUserServiceAgree())
                    .userServiceAgreeDate(now)
                    .build());
        });

        publisher.publishEvent(new UserModifyDeviceInfoEvent(
                user.getUserId(),
                httpServletRequest,
                request.getUserDeviceModel(),
                request.getUserDeviceManufacturer(),
                request.getUserOsVersion(),
                request.getUserDeviceToken()
        ));

        return UserSignUpDto.builder()
                .patientId(request.getPatientId())
                .userId(user.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userLoginId(request.getUserLoginId())
                .userName(request.getUserName())
                .userGender(request.getUserGender())
                .userBirth(request.getUserBirth())
                .build();
    }

    /**
     *  아이디 중복 확인
     */
    @Transactional(readOnly = true)
    public void loginIdCheck(String userLoginId){

        if (userRepository.findByUserLoginId(userLoginId).isPresent()){
            throw new AlreadyDataException("이미 사용 중인 아이디입니다.");
        }

    }

    /**
     *  사용자 로그인
     */
    @Transactional
    public UserLoginDto userLogin(HttpServletRequest httpServletRequest, UserLoginRequest request){

        User user = userRepository.findByUserLoginId(request.getUserLoginId())
                .orElseThrow(() -> new UnauthorizedException("아이디 혹은 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())){
            throw new UnauthorizedException("아이디 혹은 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenUtil.createToken(user, TokenType.AccessToken);
        String refreshToken = jwtTokenUtil.createToken(user, TokenType.RefreshToken);

        user.updateLogin(refreshToken);

        publisher.publishEvent(new UserModifyDeviceInfoEvent(
                user.getUserId(),
                httpServletRequest,
                request.getUserDeviceModel(),
                request.getUserDeviceManufacturer(),
                request.getUserOsVersion(),
                request.getUserDeviceToken()
        ));

        return UserLoginDto.builder()
                .userId(user.getUserId())
                .userLoginId(user.getUserLoginId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    /**
     *  사용자 비밀번호 찾기
     */
    @Transactional
    public void userFindPassword(UserFindPasswordRequest request){

        User user = userRepository.findByUserLoginId(request.getUserLoginId()).orElseThrow(() -> new NotFoundDataException("존재하지 않는 아이디입니다."));

        if (user.getFindPwdQuestionId().equals(request.getFindPwdQuestionId())){ // 입력받은 질문과 DB 정보가 일치하는 경우
            if (!user.getFindPwdAnswer().equals(request.getFindPwdAnswer())){ // 입력받은 답변과 DB 정보가 일치하지 않는 경우
                throw new UnauthorizedException("질문 혹은 답변이 일치하지 않습니다.");
            }
        } else { // 입력받은 질문과 DB 정보가 일치하지 않는 경우
            throw new UnauthorizedException("질문 혹은 답변이 일치하지 않습니다.");
        }

    }

}
