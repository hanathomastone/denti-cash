package com.kaii.dentix.domain.userOralStatus.domain;

import com.kaii.dentix.domain.oralStatus.domain.OralStatus;
import com.kaii.dentix.domain.questionnaire.domain.Questionnaire;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "userOralStatus")
public class UserOralStatus extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userOralStatusId;

    @ManyToOne
    @JoinColumn(name = "questionnaireId")
    private Questionnaire questionnaire;

    @ManyToOne
    @JoinColumn(name = "oralStatusType")
    private OralStatus oralStatus;

    public UserOralStatus(Questionnaire questionnaire, OralStatus oralStatus) {
        this.questionnaire = questionnaire;
        this.oralStatus = oralStatus;
    }
}
