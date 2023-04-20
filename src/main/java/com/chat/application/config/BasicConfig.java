package com.chat.application.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

@Configuration
@Data
@ConfigurationProperties(prefix = "service.auth", ignoreInvalidFields = false)
public class BasicConfig {

    @Value("#{${service.response.default}}")
    private Map<String, String> defaultResponse;

    private Set<String> blocklist;
}
