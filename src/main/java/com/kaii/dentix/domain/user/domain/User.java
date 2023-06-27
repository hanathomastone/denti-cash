package com.kaii.dentix.domain.user.domain;

import com.kaii.dentix.domain.type.GenderType;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    /**
     * RefreshToken, 최근 로그인 일자 업데이트
     */
    public void updateLogin(String refreshToken) {
        this.userRefreshToken = refreshToken;
        this.userLastLoginDate = new Date();
    }

}