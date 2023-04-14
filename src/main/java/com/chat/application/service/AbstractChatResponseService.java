package com.chat.application.service;

import com.chat.application.config.BasicConfig;
import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.util.UiUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class AbstractChatResponseService implements ChatResponseService{
    @Autowired
    public BasicConfig basicConfig;
    @Override
    public void getChatResponseAsync(AsyncStatusInfo asyncStatusInfo) {
        for(Map.Entry<String, String> entry: basicConfig.getDefaultResponse().entrySet()){
            if (asyncStatusInfo.getNewText().toLowerCase().contains(entry.getKey().toLowerCase())){
                asyncStatusInfo.getText().add(entry.getValue());
                UiUtil.scrollToBottomCheck(asyncStatusInfo);
                UiUtil.updateCharacter(asyncStatusInfo, entry.getValue(), Message.Role.ASSISTANT);
                return;
            }
        }
        getAiResponseAsync(asyncStatusInfo);
    }

}
