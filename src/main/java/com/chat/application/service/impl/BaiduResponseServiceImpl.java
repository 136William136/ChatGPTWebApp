package com.chat.application.service.impl;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.Message;
import com.chat.application.service.AbstractChatResponseService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("baiduResponseServiceImpl")
public class BaiduResponseServiceImpl extends AbstractChatResponseService {

    private String message = "百度AI尚未引入";

    @Override
    public String getChatResponseAsync(AsyncStatusInfo asyncStatusInfo) {
        asyncStatusInfo.getText().add(message);
        return null;
    }

    @Override
    public String getAiResponseAsync(AsyncStatusInfo asyncStatusInfo) {
        return message;
    }
    @Override
    public String getProviderName() {
        return "baidu";
    }

}
