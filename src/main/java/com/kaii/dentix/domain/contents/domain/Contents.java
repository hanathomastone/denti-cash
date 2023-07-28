package com.kaii.dentix.domain.contents.domain;

import com.kaii.dentix.domain.type.ContentsType;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  콘텐츠
 */
@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "contents")
public class Contents extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int contentsId;

    @Column(nullable = false)
    private int contentsSort;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", nullable = false)
    private ContentsType contentsType;

    @Column(nullable = false)
    private String contentsTitle;

    @Column(length = 7, nullable = false)
    private String contentsTypeColor;

    @Column(nullable = false)
    private String contentsThumbnail;

    private String contentsPath;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentsId")
    private List<ContentsToCategory> categoryIds;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentsId")
    private List<ContentsCard> contentsCards;

}
