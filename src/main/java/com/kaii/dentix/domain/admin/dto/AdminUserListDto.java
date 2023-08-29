package com.kaii.dentix.domain.admin.dto;

import com.kaii.dentix.global.common.dto.PagingDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminUserListDto {

    private PagingDTO paging;

    private List<AdminUserInfoDto> userList;

}
