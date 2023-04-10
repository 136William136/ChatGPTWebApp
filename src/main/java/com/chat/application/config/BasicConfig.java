package com.chat.application.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Data
public class BasicConfig {

    @Value("#{${service.response.default}}")
    private Map<String, String> defaultResponse;

}
