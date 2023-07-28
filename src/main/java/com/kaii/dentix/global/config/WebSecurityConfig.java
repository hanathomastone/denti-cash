package com.kaii.dentix.global.config;

import com.kaii.dentix.domain.jwt.JwtAuthenticationFilter;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.userDeviceType.application.UserDeviceTypeService;
import com.kaii.dentix.global.common.filter.VersionCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;

    private final UserDeviceTypeService userDeviceTypeService;

    public static String[] EXCLUDE_URLS = {
            "/actuator/health", // health check
            "/docs/*", // restdocs
            "/resources/edu/thumbnail/*",
            "/corps",
            "/login/*", "/login",
            "/test/",
            "/service-agreement",
            "/contents", "/contents/*"
    };

    /**
     *  비밀번호 암호화
     */
    @Bean
    public BCryptPasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.httpBasic(AbstractHttpConfigurer::disable) // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // csrf 보안 토큰 disable 처리
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests // 권한 설정
                        .requestMatchers(EXCLUDE_URLS).permitAll()
                        .anyRequest().hasAnyRole("USER")
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new VersionCheckFilter(userDeviceTypeService), JwtAuthenticationFilter.class);
        return http.build();
    }

}