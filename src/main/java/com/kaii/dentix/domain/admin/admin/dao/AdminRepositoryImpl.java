package com.kaii.dentix.domain.admin.admin.dao;

import com.kaii.dentix.domain.admin.admin.domain.QAdmin;
import com.kaii.dentix.domain.admin.admin.dto.AdminAccountDto;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.global.common.dto.PageAndSizeRequest;
import com.kaii.dentix.global.common.dto.PagingRequest;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QAdmin admin = QAdmin.admin;

    /**
     *  관리자 페이징 목록
     */
    @Override
    public Page<AdminAccountDto> findAll(PageAndSizeRequest request){

        Pageable paging = new PagingRequest(request.getPage(), request.getSize()).of();

        List<AdminAccountDto> result = queryFactory
                .select(Projections.constructor(AdminAccountDto.class,
                        admin.adminId, admin.adminIsSuper, admin.adminLoginIdentifier, admin.adminName, admin.adminPhoneNumber,
                        Expressions.stringTemplate("DATE_FORMAT({0}, {1})", admin.created, "%Y-%m-%d")
                ))
                .from(admin)
                .orderBy(admin.created.desc())
                .offset(paging.getOffset())
                .limit(paging.getPageSize())
                .fetch();

        return new PageImpl<>(result, paging, result.size());

    }

}
