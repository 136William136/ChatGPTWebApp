package com.chat.application.service.impl;

import com.chat.application.listener.OpenAiEventSourceListener;
import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.service.AbstractChatResponseService;
import com.chat.application.util.UiUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Service;

@Service("baiduResponseServiceImpl")
public class BaiduResponseServiceImpl extends AbstractChatResponseService {

    private String provider = "baidu";
    @Override
    public void getAiResponseAsync(AsyncStatusInfo asyncStatusInfo) {
        String response = "AI尚未引入";
        asyncStatusInfo.getText().add(response);
        UiUtil.scrollToBottomCheck(asyncStatusInfo);
        UiUtil.updateCharacter(asyncStatusInfo, response, Message.Role.ASSISTANT);
    }
    @Override
    public String getProviderName() {
        return provider;
    }

}
