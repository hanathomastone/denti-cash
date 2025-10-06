package com.kaii.dentix.infra.tokensync;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "token-sync")
public class TokenSyncProperties {
    private String baseUrl;            // ex) http://220.149.235.79:5303
    private Integer readTimeoutMs = 10000;
    private Integer connectTimeoutMs = 3000;
    private String cron = "0 */5 * * * *";
}