package com.chat.application.service;

import com.chat.application.model.UserQuota;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class QuotaService {

    @Value("${service.quota.max}")
    private Long maxQuota;
    private static Map<String, UserQuota> userQuotaMap = new HashMap<>();

    /* 每天0点 1分 1秒执行 */
    @Scheduled(cron = "1 1 0 * * ?")
    public void tokenJob(){
        Map<String, UserQuota> tmpIpTokens = new HashMap<>(userQuotaMap);
        log.info("开始每日Token统计: ");
        for(Map.Entry<String, UserQuota> ipToken: tmpIpTokens.entrySet()){
            log.info("IP:[{}] Token:[{}]",ipToken.getKey(), ipToken.getValue().getCurrentQuota().get());
        }
        userQuotaMap = new HashMap<>();
    }

    public int addToken(String ip, Integer amount){
        Optional.ofNullable(userQuotaMap.get(ip)).ifPresentOrElse(userQuota -> {
            userQuota.getCurrentQuota().getAndAdd(amount);
        }, () -> {
            userQuotaMap.put(ip, new UserQuota()
                    .setCurrentQuota(new AtomicLong(amount))
                    .setIp(ip)
                    .setMaxQuota(maxQuota));
        });
        return 1;
    }

    public UserQuota getQuota(String ip){
        return userQuotaMap.containsKey(ip)
                ? userQuotaMap.get(ip)
                : new UserQuota()
                .setCurrentQuota(new AtomicLong(0))
                .setMaxQuota(maxQuota).setIp(ip);
    }

    public int getQuotaPercent(String ip){
        if (userQuotaMap.containsKey(ip)){
            UserQuota userQuota = userQuotaMap.get(ip);
            Integer quotaPercent = Math.toIntExact(
                    (userQuota.getMaxQuota() - userQuota.getCurrentQuota().get()) * 100
                    / userQuota.getMaxQuota());
            return Math.max(quotaPercent, 0);
        }
        return 100;
    }
    public Map<String, UserQuota> getUserQuotaList(){
        return userQuotaMap;
    }

}
