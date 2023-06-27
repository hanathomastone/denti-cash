package com.kaii.dentix.domain.userServiceAgreement.domain;

import com.kaii.dentix.domain.type.YnType;
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
@Table(name = "userServiceAgreement")
public class UserServiceAgreement extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userServiceAgreeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long serviceAgreeId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", nullable = false)
    private YnType isUserServiceAgree;

    @Temporal(TemporalType.TIMESTAMP)
    private Date userServiceAgreeDate;

}
