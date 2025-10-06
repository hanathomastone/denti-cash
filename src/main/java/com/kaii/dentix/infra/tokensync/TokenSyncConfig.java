package com.kaii.dentix.infra.tokensync;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(TokenSyncProperties.class)
@RequiredArgsConstructor
public class TokenSyncConfig {

    private final TokenSyncProperties props;

    @Bean
    public RestTemplate tokenSyncRestTemplate() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setReadTimeout(props.getReadTimeoutMs());
        f.setConnectTimeout(props.getConnectTimeoutMs());
        return new RestTemplate(f);
    }
}