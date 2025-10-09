package com.kaii.dentix.domain.admin.dto;

import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.user.dto.TokenDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@AllArgsConstructor @NoArgsConstructor
public class AdminLoginDto extends TokenDto {

    private YnType isFirstLogin;

    private Long adminId;

    private String adminName;

    private YnType adminIsSuper;

}
