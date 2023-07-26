package com.kaii.dentix.domain.contents.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "contentsList")
public class ContentsList {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentsListId;

    private Long contentsCategoryId;

    private Long contentsId;

}
