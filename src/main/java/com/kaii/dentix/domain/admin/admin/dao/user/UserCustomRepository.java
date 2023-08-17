package com.kaii.dentix.domain.admin.admin.dao.user;

import com.kaii.dentix.domain.admin.admin.dto.AdminUserInfoDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminUserListRequest;
import org.springframework.data.domain.Page;

public interface UserCustomRepository {

    Page<AdminUserInfoDto> findAll(AdminUserListRequest request);

}
