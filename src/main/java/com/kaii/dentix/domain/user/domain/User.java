package com.kaii.dentix.domain.user.domain;

import com.kaii.dentix.domain.type.GenderType;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "user")
public class User extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 45, nullable = false)
    private String userLoginId;

    @Column(nullable = false)
    private String userPassword;

    @Column(length = 45, nullable = false)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", nullable = false)
    private GenderType userGender;

    @Column(length = 10, nullable = false)
    private String userBirth;

    @Column(nullable = false)
    private Long findPwdQuestionId;

    @Column(nullable = false)
    private String findPwdAnswer;

    private String userRefreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    public Date deleted;

    @Temporal(TemporalType.TIMESTAMP)
    public Date userLastLoginDate;

    private Long patientId;

    private Long userDeviceTypeId;

    @Column(length = 45)
    private String userDeviceModel;

    @Column(length = 45)
    private String userDeviceManufacturer;

    @Column(length = 45)
    private String userOsVersion;

    private String userDeviceToken;

    @Column(length = 10)
    private String userAppVersion;


    /**
     * RefreshToken, 최근 로그인 일자 업데이트
     */
    public void updateLogin(String refreshToken) {
        this.userRefreshToken = refreshToken;
        this.userLastLoginDate = new Date();
    }

    /**
     * 디바이스 정보 업데이트
     */
    public void modifyDeviceInfo(Long userDeviceTypeId, String userAppVersion, String userDeviceModel, String userDeviceManufacturer, String userOsVersion, String userDeviceToken) {
        this.userDeviceTypeId = userDeviceTypeId;
        this.userAppVersion = userAppVersion;
        this.userDeviceModel = userDeviceModel;
        this.userDeviceManufacturer = userDeviceManufacturer;
        this.userOsVersion = userOsVersion;
        this.userDeviceToken = userDeviceToken;
    }

    /**
     *  비밀번호 수정
     */
    public void modifyUserPassword(PasswordEncoder passwordEncoder, String userPassword) {
        this.userPassword = passwordEncoder.encode(userPassword);
    }

    /**
     *  질문과 답변 수정
     */
    public void modifyQnA(Long findPwdQuestionId, String findPwdAnswer){
        this.findPwdQuestionId = findPwdQuestionId;
        this.findPwdAnswer = findPwdAnswer;
    }

    /**
     *  회원 정보 수정
     */
    public void modifyInfo(String userName, GenderType userGender, String userBirth){
        this.userName = userName;
        this.userGender = userGender;
        this.userBirth = userBirth;
    }

    /**
     *  로그아웃
     */
    public void logout(){
        this.userDeviceToken = null;
    }

}