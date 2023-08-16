package com.kaii.dentix.domain.admin.admin.dao;

import com.kaii.dentix.domain.admin.admin.dto.AdminAccountDto;
import com.kaii.dentix.global.common.dto.PageAndSizeRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     *  관리자 페이징 목록
     */
    @Override
    public Page<AdminAccountDto> findAll(PageAndSizeRequest request){

    }

}
