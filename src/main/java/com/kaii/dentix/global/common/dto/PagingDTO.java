package com.kaii.dentix.global.common.dto;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PagingDTO {

    private int number; // 현재 페이지

    private int totalPages; // 전체 페이지수

    private long totalElements; // 전체 데이터 개수

    public int getNumber() {
        return number + 1;
    }

}
