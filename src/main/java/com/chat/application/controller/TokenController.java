package com.chat.application.controller;

import com.chat.application.job.TokenJob;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token/check")
@Slf4j
public class TokenController {

    @GetMapping(value = "/getSpent")
    public String getTokenSpent() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(TokenJob.ipTokens);
        return json;
    }

}
