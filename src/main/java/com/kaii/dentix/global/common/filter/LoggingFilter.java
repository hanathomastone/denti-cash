package com.kaii.dentix.global.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.jwt.TokenType;
import com.kaii.dentix.domain.systemLog.dao.SystemLogRepository;
import com.kaii.dentix.domain.systemLog.domain.SystemLog;
import com.kaii.dentix.domain.type.UserRole;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private final ObjectMapper objectMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final SystemLogRepository systemLogRepository;

    /**
     * 필터 실행 부분 입니다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info(" ::: LoggingFilter ::: ");

        ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        chain.doFilter(httpServletRequest, httpServletResponse);

        if (!httpServletRequest.getRequestURI().equals("/login/access-token")) {
            httpServletResponse.copyBodyToResponse();
            return;
        }

        // 후처리
        // 헤더 정보 추출
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, httpServletRequest.getHeader(headerName));
        }

        // 토큰 정보 추출
        Long tokenUserId = null;
        UserRole tokenUserRole = null;

        try {
            if (headers.containsKey("authorization")) {
                String header = headers.get("authorization");
                tokenUserId = jwtTokenUtil.getUserId(header, TokenType.AccessToken);
                tokenUserRole = jwtTokenUtil.getRoles(header, TokenType.AccessToken);
            } else if (headers.containsKey("refreshtoken")) {
                String header = headers.get("refreshtoken");
                tokenUserId = jwtTokenUtil.getUserId(header, TokenType.RefreshToken);
                tokenUserRole = jwtTokenUtil.getRoles(header, TokenType.RefreshToken);
            }
        } catch (Exception ignored) {}

        String requestBody = new String(httpServletRequest.getContentAsByteArray());
        String responseBody = new String(httpServletResponse.getContentAsByteArray());
        if (StringUtils.isBlank(responseBody)) {
            responseBody = objectMapper.writeValueAsString(httpServletRequest.getParameterMap());
        }

        systemLogRepository.save(SystemLog.builder()
            .tokenUserId(tokenUserId)
            .tokenUserRole(tokenUserRole)
            .requestUrl(httpServletRequest.getRequestURL().toString())
            .header(objectMapper.writeValueAsString(headers))
            .requestBody(StringUtils.isNotBlank(requestBody) ? requestBody : null)
            .responseBody(StringUtils.isNotBlank(responseBody) ? responseBody : null)
            .build());

        httpServletResponse.copyBodyToResponse();
    }
}
