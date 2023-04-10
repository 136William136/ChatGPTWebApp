package com.chat.application.service;

import com.chat.application.model.Message;

import java.util.List;
import java.util.Map;

public interface ChatResponseService {

    String getChatResponse(List<Message> messageList
            , String newText, String model);

    String getAiResponse(List<Message> messageList
            , String model);

    String getProviderName();

}
