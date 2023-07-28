package com.kaii.dentix.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@AllArgsConstructor
public class UserLoginDto extends TokenDto{

    private Long userId;

    private String userName;

}
