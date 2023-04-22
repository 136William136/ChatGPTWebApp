package com.chat.application.service.impl;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.service.AbstractChatResponseService;
import com.chat.application.service.UiService;
import com.chat.application.util.UiUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service("baiduResponseServiceImpl")
public class BaiduResponseServiceImpl extends AbstractChatResponseService {

    @Getter
    private String providerName = "baidu";
    @Override
    public void getAiResponseAsync(AsyncStatusInfo asyncStatusInfo) {
        String response = "AI尚未引入";
        asyncStatusInfo.getText().add(response);
        UiUtil.scrollToBottomCheck(asyncStatusInfo);
        super.getUiService().updateCharacter(asyncStatusInfo, response, Message.Role.ASSISTANT, false);
    }

}
