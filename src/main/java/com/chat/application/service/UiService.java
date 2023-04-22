package com.chat.application.service;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.util.JsScriptUtil;
import com.chat.application.util.RequestUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@org.springframework.stereotype.Component
public class UiService {

    @Autowired
    private QuotaService quotaService;

    public void updateCharacter(AsyncStatusInfo asyncStatusInfo, String fullContent, Message.Role role, Boolean addToken){
        asyncStatusInfo.getSendButton().setEnabled(true);
        asyncStatusInfo.getMessageList()
                .add(Message.builder()
                        .role(role)
                        .content(fullContent)
                        .build());
        asyncStatusInfo.getUi().getSession()
                .setAttribute(asyncStatusInfo.getUiContextKey()
                        , asyncStatusInfo.getMessageList());
        asyncStatusInfo.getUi().push();

        /* 统计 */
        Integer tokens = addToken
                ? addToken(asyncStatusInfo, asyncStatusInfo.getMessageList())
                : 0;
        log.info("IP:[{}], Token消耗:[{}], 问题: [{}], 回答人: [{}], 答案: [{}]",asyncStatusInfo.getIp()
                , tokens
                , asyncStatusInfo.getMessageList()
                        .get(asyncStatusInfo.getMessageList().size() - 2)
                        .getContent()
                , asyncStatusInfo.getModel().getProvider()
                , fullContent);
    }

    public Integer addToken(AsyncStatusInfo asyncStatusInfo, List<Message> messageList){
        Integer tokens = TikTokensUtil.tokens(asyncStatusInfo.getModel().getModelName()
                , messageList);
        quotaService.addToken(asyncStatusInfo.getIp(), tokens);
        /* 展示当前额度 */
        Integer quotaPercent = quotaService.getQuotaPercent(asyncStatusInfo.getIp());
        asyncStatusInfo.getUi().getPage().executeJs(JsScriptUtil.updateQuotaLevel(quotaPercent));
        return tokens;
    }

}
