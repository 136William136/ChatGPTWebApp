package com.chat.application.service;

import com.chat.application.config.BasicConfig;
import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.Message;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public abstract class AbstractChatResponseService implements ChatResponseService{
    @Autowired
    public BasicConfig basicConfig;
    @Override
    public String getChatResponseAsync(AsyncStatusInfo asyncStatusInfo) {
        for(Map.Entry<String, String> entry: basicConfig.getDefaultResponse().entrySet()){
            if (asyncStatusInfo.getNewText().toLowerCase().contains(entry.getKey().toLowerCase())){
                return entry.getValue();
            }
        }
        return getAiResponseAsync(asyncStatusInfo);
    }

}
