package com.kaii.dentix.domain.oralStatus.domain;

import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oralStatus")
public class OralStatus extends TimeEntity {

    @Id
    private String oralStatusType;

    @Column(nullable = false)
    private String oralStatusTitle;

    @Column(nullable = false)
    private String oralStatusDescription;

    public OralStatus(String oralStatusType) {
        this.oralStatusType = oralStatusType;
    }
}
