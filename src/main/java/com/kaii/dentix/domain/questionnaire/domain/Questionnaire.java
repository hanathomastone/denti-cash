package com.kaii.dentix.domain.questionnaire.domain;

import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "questionnaire")
public class Questionnaire extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionnaireId;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "form", columnDefinition = "json")
    private String form;

    public Questionnaire(Long userId, String form) {
        this.userId = userId;
        this.form = form;
    }
}
