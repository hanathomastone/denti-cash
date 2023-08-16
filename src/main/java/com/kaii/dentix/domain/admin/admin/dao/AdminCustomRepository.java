package com.kaii.dentix.domain.admin.admin.dao;

import com.kaii.dentix.domain.admin.admin.dto.AdminAccountDto;
import com.kaii.dentix.global.common.dto.PageAndSizeRequest;
import org.springframework.data.domain.Page;

public interface AdminCustomRepository {

    Page<AdminAccountDto> findAll(PageAndSizeRequest request);

}
