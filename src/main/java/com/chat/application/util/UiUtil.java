package com.chat.application.util;

import com.chat.application.model.AsyncStatusInfo;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UiUtil {

    public static void updateCharacter(AsyncStatusInfo asyncStatusInfo, String fullContent, Message.Role role){
        asyncStatusInfo.getButton().setEnabled(true);
        asyncStatusInfo.getMessageList()
                .add(Message.builder()
                        .role(role)
                        .content(fullContent)
                        .build());
        asyncStatusInfo.getUi().getSession()
                .setAttribute(asyncStatusInfo.getUiContextKey()
                        , asyncStatusInfo.getMessageList());
        asyncStatusInfo.getUi().push();
        log.info("IP:[{}], 问题: [{}], 回答人: [{}], 答案: [{}]",asyncStatusInfo.getIp()
                , asyncStatusInfo.getMessageList()
                        .get(asyncStatusInfo.getMessageList().size() - 2)
                        .getContent()
                , asyncStatusInfo.getModel().getProvider()
                , fullContent);
    }

}
