package com.example.dentix.domain.user.domain;

import com.example.dentix.domain.GenderType;
import com.example.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
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
    private Long userFindPwdQuestion;

    @Column(nullable = false)
    private String userFindPwdAnswer;

    private String userRefreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    public Date deleted;

    @Temporal(TemporalType.TIMESTAMP)
    public Date userLastLoginDate;

    private Long patientId;

}