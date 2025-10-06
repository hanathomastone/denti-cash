package com.kaii.dentix.domain.user.application;

import com.kaii.dentix.domain.admin.dao.AdminRepository;
import com.kaii.dentix.domain.admin.domain.Admin;
import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.domain.findPwdQuestion.dao.FindPwdQuestionRepository;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementDto;
import com.kaii.dentix.domain.type.UserRole;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.patient.dao.PatientRepository;
import com.kaii.dentix.domain.patient.domain.Patient;
import com.kaii.dentix.domain.serviceAgreement.application.ServiceAgreementService;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.domain.user.dto.*;
import com.kaii.dentix.domain.user.dto.request.*;
import com.kaii.dentix.domain.user.event.UserModifyDeviceInfoEvent;
import com.kaii.dentix.domain.userServiceAgreement.dao.UserServiceAgreementRepository;
import com.kaii.dentix.domain.userServiceAgreement.domain.UserServiceAgreement;
import com.kaii.dentix.domain.blockChain.wallet.application.WalletService;
import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.global.common.error.exception.*;
import com.kaii.dentix.global.common.util.CryptoUtil;
import com.kaii.dentix.global.flask.client.FlaskClient;
import com.kaii.dentix.global.flask.dto.FlaskCreateWalletResponse;
import com.kaii.dentix.global.flask.dto.FlaskPrivateKeyResponse;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserLoginService {
    private final UserWalletRepository userWalletRepository;
    private final WalletService walletService;

    private final PatientRepository patientRepository;
    private final FlaskClient flaskClient;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ServiceAgreementService serviceAgreementService;

    private final UserServiceAgreementRepository userServiceAgreementRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final FindPwdQuestionRepository findPwdQuestionRepository;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher publisher;

    private final AdminRepository adminRepository;
    private final CryptoUtil cryptoUtil;
    private final TokenContractRepository tokenContractRepository;
    /**
     * 사용자 서비스 이용동의 여부 확인 및 저장
     */
    public void userServiceAgreeCheckAndSave(List<Long> request, Long userId){
        List<ServiceAgreementDto> serviceAgreementList = serviceAgreementService.serviceAgreementList().getServiceAgreement();

        if (request.stream().anyMatch(serviceAgreementId -> serviceAgreementList.stream().noneMatch(requestId -> requestId.getId().equals(serviceAgreementId)))) {
            throw new NotFoundDataException("존재하지 않는 서비스 이용 동의입니다.");
        }

        Date now = new Date();

        serviceAgreementList.forEach(serviceAgreementDTO -> {
            if (serviceAgreementDTO.getIsServiceAgreeRequired().equals(YnType.Y) && !request.contains(serviceAgreementDTO.getId())) {
                throw new BadRequestApiException(serviceAgreementDTO.getName() + "는(은) 필수 항목입니다.");
            }

            userServiceAgreementRepository.save(UserServiceAgreement.builder()
                    .userId(userId)
                    .serviceAgreeId(serviceAgreementDTO.getId())
                    .isUserServiceAgree(request.contains(serviceAgreementDTO.getId()) ? YnType.Y : YnType.N)
                    .userServiceAgreeDate(now)
                    .build());
        });

    }

    /**
     *  사용자 회원 확인
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVerifyDto userVerify(UserVerifyRequest request){

        List<Patient> patientList = patientRepository.findByPatientPhoneNumberOrPatientName(request.getPatientPhoneNumber(), request.getPatientName());

        Patient patient = patientList.stream()
                .filter(p -> p.getPatientName().equals(request.getPatientName()) && p.getPatientPhoneNumber().equals(request.getPatientPhoneNumber()))
                .findAny().orElse(null);

        // 이미 가입된 사용자의 경우
        if (patient != null && userRepository.findByPatientId(patient.getPatientId()).isPresent()) throw new AlreadyDataException("이미 가입한 사용자입니다.");

        if (patient == null) {
            Optional<Patient> isExistPatientPhoneNumber = patientList.stream()
                    .filter(p -> p.getPatientPhoneNumber().equals(request.getPatientPhoneNumber()))
                    .findAny();

            if (isExistPatientPhoneNumber.isPresent()){
                Long patientId = isExistPatientPhoneNumber.get().getPatientId();
                if (userRepository.findByPatientId(patientId).isPresent()) throw new BadRequestApiException("이미 사용중인 번호에요.\n번호를 다시 확인해 주세요.");  // 동일한 연락처가 이미 가입되어 있는 경우

                throw new UnauthorizedException("회원 정보가 일치하지 않아요.\n다시 확인해 주세요."); // 연락처 일치 && 실명 불일치
            }
        }

        return UserVerifyDto.builder()
                .patientId(patient != null ? patient.getPatientId() : null) // true : 인증된 사용자, false : 미인증 사용자
                .build();
    }


    @Transactional
    public UserSignUpDto userSignUp(HttpServletRequest httpServletRequest, UserSignUpRequest request){

        if (request.getPatientId() != null) { // 인증된 회원인 경우
            patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new NotFoundDataException("존재하지 않는 회원입니다."));
            if (userRepository.findByPatientId(request.getPatientId()).isPresent())
                throw new AlreadyDataException("이미 가입한 사용자입니다.");
        }

        // 아이디 중복 확인
        this.loginIdCheck(request.getUserLoginIdentifier());

        // 올바르지 않은 findPwdQuestionId 인 경우
        if (findPwdQuestionRepository.findById(request.getFindPwdQuestionId()).isEmpty())
            throw new NotFoundDataException("존재하지 않는 질문입니다.");

        // ✅ User 저장
        User user = userRepository.save(User.builder()
                .userLoginIdentifier(request.getUserLoginIdentifier())
                .userName(request.getUserName())
                .userGender(request.getUserGender())
                .userPassword(passwordEncoder.encode(request.getUserPassword()))
                .findPwdQuestionId(request.getFindPwdQuestionId())
                .findPwdAnswer(request.getFindPwdAnswer())
                .birth(request.getBirth())
                .patientId(request.getPatientId())
                .isVerify(request.getPatientId() == null ? YnType.N : YnType.Y)
                .build());

        Long userId = user.getUserId();


        // ✅ 토큰 발급
        String accessToken = jwtTokenUtil.createToken(user, TokenType.AccessToken);
        String refreshToken = jwtTokenUtil.createToken(user, TokenType.RefreshToken);

        user.updateLogin(refreshToken);

        // ✅ 서비스 이용 동의 저장
        this.userServiceAgreeCheckAndSave(request.getUserServiceAgreementRequest(), userId);

        // ✅ 디바이스 이벤트 발행
        publisher.publishEvent(new UserModifyDeviceInfoEvent(
                user.getUserId(),
                httpServletRequest,
                request.getUserDeviceModel(),
                request.getUserDeviceManufacturer(),
                request.getUserOsVersion(),
                request.getUserDeviceToken()
        ));

        // ✅ 활성화된 컨트랙트 조회
        TokenContract defaultContract = tokenContractRepository.findActiveContract()
                .orElseThrow(() -> new RuntimeException("활성화된 토큰 컨트랙트가 없습니다."));

        //지갑 생성 요청
        FlaskCreateWalletResponse walletRes = flaskClient.createWallet();
        if (walletRes.getAddress() == null)
            throw new RuntimeException("Flask에서 address를 반환하지 않았습니다.");
        String walletAddress = walletRes.getAddress();

        // 3️⃣ UserWallet 임시 저장 (address만)
        UserWallet wallet = UserWallet.builder()
                .user(user)
                .walletAddress(walletAddress)
                .encryptedPrivateKey(null)
                .balance(0L)
                .contract(defaultContract)   // ✅ 추가
                .active(true)
                .build();
//        userWalletRepository.save(wallet);

        // 4️⃣ Flask - privateKey 조회
        FlaskPrivateKeyResponse keyRes = flaskClient.getPrivateKey(walletAddress);
        if (keyRes.getPrivate_key() == null)
            throw new RuntimeException("privateKey 조회 실패");

        // 5️⃣ privateKey 암호화 후 저장
        String encryptedKey = cryptoUtil.encrypt(keyRes.getPrivate_key());
        wallet.updateEncryptedPrivateKey(encryptedKey);
        userWalletRepository.save(wallet);

        // ✅ 결과 반환
        return UserSignUpDto.builder()
                .patientId(request.getPatientId())
                .userId(user.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userLoginIdentifier(request.getUserLoginIdentifier())
                .userName(request.getUserName())
                .userGender(request.getUserGender())
                .birth(user.getBirth())
                .build();
    }
    /**
     *  아이디 중복 확인
     */
    @Transactional(readOnly = true)
    public void loginIdCheck(String userLoginIdentifier){

        if (userRepository.findByUserLoginIdentifier(userLoginIdentifier).isPresent()){
            throw new AlreadyDataException("이미 사용 중인 아이디입니다.");
        }

    }

    /**
     *  사용자 로그인
     */
    @Transactional
    public UserLoginDto userLogin(HttpServletRequest httpServletRequest, UserLoginRequest request){

        User user = userRepository.findByUserLoginIdentifier(request.getUserLoginIdentifier())
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
                .userName(user.getUserName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    /**
     *  사용자 비밀번호 찾기
     */
    @Transactional
    public UserFindPasswordDto userFindPassword(UserFindPasswordRequest request){

        User user = userRepository.findByUserLoginIdentifier(request.getUserLoginIdentifier()).orElseThrow(() -> new NotFoundDataException("존재하지 않는 아이디입니다."));

        if (user.getFindPwdQuestionId().equals(request.getFindPwdQuestionId())){ // 입력받은 질문과 DB 정보가 일치하는 경우
            if (!user.getFindPwdAnswer().equals(request.getFindPwdAnswer())){ // 입력받은 답변과 DB 정보가 일치하지 않는 경우
                throw new UnauthorizedException("질문 혹은 답변이 일치하지 않습니다.");
            }
        } else { // 입력받은 질문과 DB 정보가 일치하지 않는 경우
            throw new UnauthorizedException("질문 혹은 답변이 일치하지 않습니다.");
        }

        return UserFindPasswordDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userLoginIdentifier(user.getUserLoginIdentifier())
                .build();

    }

    /**
     *  사용자 비밀번호 재설정
     */
    @Transactional
    public void userModifyPassword(UserModifyPasswordRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundDataException("존재하지 않는 회원입니다."));
        user.modifyUserPassword(passwordEncoder, request.getUserPassword());
    }

    /**
     *  AccessToken 재발급
     */
    public AccessTokenDto accessTokenReissue(HttpServletRequest request) {

        String refreshToken = jwtTokenUtil.getRefreshToken(request);

        if (jwtTokenUtil.isExpired(refreshToken, TokenType.RefreshToken)) throw new TokenExpiredException();
        Long roleId = jwtTokenUtil.getUserId(refreshToken, TokenType.RefreshToken);
        UserRole roles = jwtTokenUtil.getRoles(refreshToken, TokenType.RefreshToken);

        switch (roles) {
            case ROLE_USER:
                User user = userRepository.findById(roleId).orElseThrow(TokenExpiredException::new);
                if (StringUtils.isBlank(user.getUserRefreshToken()) || !user.getUserRefreshToken().equals(refreshToken))
                    throw new UnauthorizedException();

                return AccessTokenDto.builder().accessToken(jwtTokenUtil.createToken(user, TokenType.AccessToken)).build();
            case ROLE_ADMIN:
                Admin admin = adminRepository.findById(roleId).orElseThrow(TokenExpiredException::new);
                if (StringUtils.isBlank(admin.getAdminRefreshToken()) || !admin.getAdminRefreshToken().equals(refreshToken))
                    throw new UnauthorizedException();

                return AccessTokenDto.builder().accessToken(jwtTokenUtil.createToken(admin, TokenType.AccessToken)).build();

            default:
                throw new TokenExpiredException();
        }
    }

}
