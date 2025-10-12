package com.kaii.dentix.domain.blockChain.token.controller;


import com.kaii.dentix.domain.blockChain.token.application.TokenLedgerService;
import com.kaii.dentix.domain.blockChain.token.dto.TokenLedgerDto;
import com.kaii.dentix.domain.blockChain.token.dto.UserTokenSummaryDto;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenLedgerController {

    private final TokenLedgerService tokenLedgerService;
    private final UserService userService;

    /**
     * 내 토큰 거래내역 조회
     */
    @GetMapping("/my")
    public ResponseEntity<DataResponse<List<TokenLedgerDto>>> getMyTokenHistory(HttpServletRequest request) {
        User user = userService.getTokenUser(request);
        List<TokenLedgerDto> history = tokenLedgerService.getUserTokenHistory(user.getUserId());
        return ResponseEntity.ok(new DataResponse<>(200, "토큰 거래내역 조회 성공", history));
    }


    /**
     * 사용자 잔여 토큰 조회 API
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getUserTokenBalance(@RequestParam Long userId) {
        Long balance = tokenLedgerService.getUserTokenBalance(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("balance", balance);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 토큰 요약 조회 (잔액 + 적립내역 + 사용내역)
     */
    @GetMapping("/summary")
    public DataResponse<UserTokenSummaryDto> getUserTokenSummary(@RequestParam Long userId) {
        UserTokenSummaryDto response = tokenLedgerService.getUserTokenSummary(userId);
        return new DataResponse<>(response);
    }

    @GetMapping("/summary/reward")
    public DataResponse<UserTokenSummaryDto> getUserRewardLedgers(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "ALL") String period,   // 전체, 1D, 3D, 7D
            @RequestParam(defaultValue = "DESC") String sort     // 최신순(DESC), 오래된순(ASC)
    ) {
        UserTokenSummaryDto result = tokenLedgerService.getUserRewardLedgers(userId, period, sort);
        return new DataResponse<>(result);
    }

    @GetMapping("/summary/use")
    public DataResponse<UserTokenSummaryDto> getUserUseLedgers(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "ALL") String period,
            @RequestParam(defaultValue = "DESC") String sort
    ) {
        UserTokenSummaryDto result = tokenLedgerService.getUserUseLedgers(userId, period, sort);
        return new DataResponse<>(result);
    }
}