package com.chat.application.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class TokenJob {

    public static Map<String, AtomicLong> ipTokens = new HashMap<>();
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void tokenJob(){
        Map<String, AtomicLong> tmpIpTokens = new HashMap<>(ipTokens);
        log.info("开始每日Token统计: ");
        for(Map.Entry<String, AtomicLong> ipToken: tmpIpTokens.entrySet()){
            log.info("IP:[{}] Token:[{}]",ipToken.getKey(), ipToken.getValue().get());
        }
        ipTokens = new HashMap<>();
    }

}
