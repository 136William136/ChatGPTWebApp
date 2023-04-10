package com.chat.application.service.impl;

import com.chat.application.config.RestConfig;
import com.chat.application.model.Message;
import com.chat.application.model.OpenaiResponse;
import com.chat.application.service.AbstractChatResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("openAiResponseServiceImpl")
@Slf4j
public class OpenAiResponseServiceImpl extends AbstractChatResponseService {

    @Autowired
    private RestConfig restConfig;
    @Value("${service.key.openai}")
    private String openaiKey;
    private String openaiUrl = "https://api.openai.com/v1/chat/completions";
    @Override
    public String getAiResponse(List<Message> messageList
            , String model){
        /* body */
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages",messageList);
        /* header */
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(){{
            add("Content-Type", "application/json");
            add("Authorization", "Bearer " + openaiKey);
        }};

        HttpEntity httpEntity = new HttpEntity(body, headers);
        try {
            ResponseEntity responseEntity = restConfig.restTemplate()
                    .exchange(openaiUrl, HttpMethod.POST, httpEntity, String.class);
            OpenaiResponse response = new ObjectMapper()
                    .readValue(responseEntity.getBody().toString(), OpenaiResponse.class);
            return response.getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
        }catch (Exception e){
            log.error("请求[{}]失败",openaiUrl, e);
            return "当前请求较多，请稍后";
        }
    }

    @Override
    public String getProviderName() {
        return "openai";
    }

}
