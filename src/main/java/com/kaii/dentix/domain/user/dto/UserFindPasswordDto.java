package com.kaii.dentix.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class UserFindPasswordDto {

    private Long userId;

}
