package com.kaii.dentix.domain.admin.admin.domain;

import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.Date;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "admin")
@Where(clause = "deleted IS NULL")
public class Admin extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @Column(length = 45, nullable = false)
    private String adminName;

    @Column(length = 45, nullable = false)
    private String adminLoginIdentifier;

    @Column(length = 11, nullable = false)
    private String adminPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", nullable = false)
    private YnType adminIsSuper;

    @Temporal(TemporalType.TIMESTAMP)
    private Date adminLastLoginDate;

    private String adminPassword;

    private String adminRefreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deleted;

}
