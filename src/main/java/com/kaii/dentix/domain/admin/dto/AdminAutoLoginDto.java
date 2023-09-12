package com.kaii.dentix.domain.admin.dto;

import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.user.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@AllArgsConstructor @NoArgsConstructor
public class AdminAutoLoginDto extends TokenDto {

    private Long adminId;

    private String adminName;

    private YnType adminIsSuper;

}
