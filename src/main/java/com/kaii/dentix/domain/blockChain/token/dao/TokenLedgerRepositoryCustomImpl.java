package com.kaii.dentix.domain.blockChain.token.dao;

import com.kaii.dentix.domain.blockChain.token.domain.QTokenLedger;
import com.kaii.dentix.domain.blockChain.token.dto.AdminTokenLedgerListRequest;
import com.kaii.dentix.domain.blockChain.token.dto.TokenLedgerResponse;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.user.domain.QUser;
import com.kaii.dentix.global.common.dto.PagingRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class TokenLedgerRepositoryCustomImpl implements TokenLedgerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QTokenLedger tokenLedger = QTokenLedger.tokenLedger;
    private final QUser user = QUser.user;

    @Override
    public Page<TokenLedgerResponse> findAllWithFilter(AdminTokenLedgerListRequest request) {
        Pageable paging = new PagingRequest(
                request.getPageSafe(),
                request.getSizeSafe()
        ).of();

        BooleanBuilder where = new BooleanBuilder();

        // ✅ 검색어 (아이디 or 이름)
        if (request.getUserIdentifierOrName() != null && !request.getUserIdentifierOrName().isBlank()) {
            where.and(
                    user.userLoginIdentifier.containsIgnoreCase(request.getUserIdentifierOrName())
                            .or(user.userName.containsIgnoreCase(request.getUserIdentifierOrName()))
            );
        }

        // ✅ 구분 (발급 / 회수)
        if ("발급".equals(request.getType())) {
            where.and(tokenLedger.type.in(
                    TokenLedgerType.ISSUE,
                    TokenLedgerType.REWARD,
                    TokenLedgerType.MANUAL,
                    TokenLedgerType.TRANSFER
            ));
        } else if ("회수".equals(request.getType())) {
            where.and(tokenLedger.type.eq(TokenLedgerType.RETRIEVE));
        }

        // ✅ 기간 필터 (오늘, 1주일, 1개월, 3개월, 1년)
        if (request.getPeriod() != null && !"ALL".equalsIgnoreCase(request.getPeriod())) {
            LocalDate now = LocalDate.now();
            LocalDate from = switch (request.getPeriod()) {
                case "TODAY" -> now.minusDays(1);
                case "WEEK1" -> now.minusWeeks(1);
                case "MONTH1" -> now.minusMonths(1);
                case "MONTH3" -> now.minusMonths(3);
                case "YEAR1" -> now.minusYears(1);
                default -> now.minusYears(10);
            };
            where.and(tokenLedger.created.after(Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }

        // ✅ 직접 입력 날짜 (startDate, endDate)
        if (request.getStartDate() != null) {
            where.and(tokenLedger.created.goe(java.sql.Date.valueOf(request.getStartDate())));
        }
        if (request.getEndDate() != null) {
            where.and(tokenLedger.created.loe(java.sql.Date.valueOf(request.getEndDate())));
        }

        // ✅ 쿼리 실행
        var query = queryFactory
                .select(Projections.constructor(TokenLedgerResponse.class,
                        tokenLedger.id,
                        tokenLedger.type.stringValue(),
                        user.userName,
                        user.userLoginIdentifier,
                        tokenLedger.message,
                        tokenLedger.memo,
                        tokenLedger.amount,
                        tokenLedger.status.stringValue(),
                        tokenLedger.created
                ))
                .from(tokenLedger)
                .leftJoin(tokenLedger.receiverUserWallet.user, user)
                .where(where)
                .orderBy("ASC".equalsIgnoreCase(request.getSort())
                        ? tokenLedger.created.asc()
                        : tokenLedger.created.desc())
                .offset(paging.getOffset())
                .limit(paging.getPageSize());

        List<TokenLedgerResponse> result = query.fetch();

        // ✅ count 쿼리
        long total = Optional.ofNullable(
                queryFactory.select(tokenLedger.count())
                        .from(tokenLedger)
                        .leftJoin(tokenLedger.receiverUserWallet.user, user)
                        .where(where)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(result, paging, total);
    }
}