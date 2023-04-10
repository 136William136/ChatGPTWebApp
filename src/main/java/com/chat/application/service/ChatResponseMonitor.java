package com.chat.application.service;


import com.chat.application.model.AiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatResponseMonitor {
    @Autowired
    List<ChatResponseService> chatResponseServiceList;

    public ChatResponseService getChatResponseService(String provider){

        for(ChatResponseService chatResponseService: chatResponseServiceList){
            if (chatResponseService.getProviderName().equalsIgnoreCase(provider)){
                return chatResponseService;
            }
        }
        throw new RuntimeException("No Chat Provider Available For " + provider);
    }

}
