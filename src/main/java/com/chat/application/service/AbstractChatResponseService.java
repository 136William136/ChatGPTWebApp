package com.chat.application.service;

import com.chat.application.config.BasicConfig;
import com.chat.application.model.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public abstract class AbstractChatResponseService implements ChatResponseService{

    @Autowired
    public BasicConfig basicConfig;
    @Override
    public String getChatResponse(List<Message> messageList
            , String newText, String modelName) {
        for(Map.Entry<String, String> entry: basicConfig.getDefaultResponse().entrySet()){
            if (newText.toLowerCase().contains(entry.getKey().toLowerCase())){
                return entry.getValue();
            }
        }
        return getAiResponse(messageList, modelName);
    }

}
