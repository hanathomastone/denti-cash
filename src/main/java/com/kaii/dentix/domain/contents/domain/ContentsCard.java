package com.kaii.dentix.domain.contents.domain;

import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "contentsCard")
public class ContentsCard extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentsCardId;

    @Column(nullable = false)
    private Long contentsId;

    @Column(nullable = false)
    private int contentsCardNumber;

    @Column(nullable = false)
    private String contentsCardPath;

}
