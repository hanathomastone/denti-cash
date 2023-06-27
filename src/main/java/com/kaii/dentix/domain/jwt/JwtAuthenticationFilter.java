package com.kaii.dentix.domain.jwt;

import com.kaii.dentix.global.common.error.ErrorResponse;
import com.kaii.dentix.global.common.error.exception.TokenExpiredException;
import com.kaii.dentix.global.common.response.ResponseMessage;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.kaii.dentix.global.config.WebSecurityConfig.EXCLUDE_URLS;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info(" ::: JwtAuthenticationFilter ::: ");

        String projectName = "/dentix";
        String requestURI = request.getRequestURI().startsWith(projectName) ? request.getRequestURI().substring(projectName.length()) : request.getRequestURI();

        boolean permitAll = Arrays.stream(EXCLUDE_URLS)
            .anyMatch(url ->
                url.endsWith("*") ? requestURI.startsWith(url.substring(0, url.length() - 1)) : requestURI.equals(url)
            );

        if (!permitAll) {
            try {
                // 헤더에서 JWT 를 받아옵니다.
                String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

                if (StringUtils.isBlank(accessToken)) throw new TokenExpiredException();
                if (jwtTokenUtil.isExpired(accessToken, TokenType.AccessToken)) throw new TokenExpiredException();
                if (jwtTokenUtil.isUnauthorized(accessToken, TokenType.AccessToken)) throw new TokenExpiredException();

                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                Authentication authentication = jwtTokenUtil.getAuthentication(accessToken, TokenType.AccessToken);
                if (authentication != null) { // 테스트 코드 대응
                    // SecurityContext 에 Authentication 객체를 저장합니다.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                ErrorResponse.of(response, HttpStatus.FORBIDDEN, ResponseMessage.FORBIDDEN_MSG);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}