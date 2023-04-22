package com.chat.application.service;

import com.chat.application.config.BasicConfig;
import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.UserQuota;
import com.chat.application.util.UiUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class AbstractChatResponseService implements ChatResponseService{
    @Autowired
    private BasicConfig basicConfig;

    @Autowired
    @Getter
    private UiService uiService;

    @Autowired
    private QuotaService quotaService;

    @Override
    public void getChatResponseAsync(AsyncStatusInfo asyncStatusInfo) {
        /* 检查IP封禁 */
        if (basicConfig.getBlocklist().contains(asyncStatusInfo.getIp())){
            sendDefaultResponse("blocked",asyncStatusInfo);
            return;
        }
        /* 检查额度消耗 */
        UserQuota userQuota = quotaService.getQuota(asyncStatusInfo.getIp());
        if (userQuota.getCurrentQuota().get() >= userQuota.getMaxQuota()){
            sendDefaultResponse("额度不足",asyncStatusInfo);
            return;
        }
        /* 检查敏感内容 */
        for(Map.Entry<String, String> entry: basicConfig.getDefaultResponse().entrySet()){
            if (asyncStatusInfo.getNewText().toLowerCase().contains(entry.getKey().toLowerCase())){
                sendDefaultResponse(entry.getValue(), asyncStatusInfo);
                return;
            }
        }
        getAiResponseAsync(asyncStatusInfo);
    }

    private void sendDefaultResponse(String defaultResponse, AsyncStatusInfo asyncStatusInfo){
        asyncStatusInfo.getText().add(defaultResponse);
        UiUtil.scrollToBottomCheck(asyncStatusInfo);
        uiService.updateCharacter(asyncStatusInfo, defaultResponse, Message.Role.ASSISTANT, false);
    }

}
