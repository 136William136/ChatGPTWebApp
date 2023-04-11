package com.chat.application.service.impl;

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
    public String getChatResponseAsync(List<Message> messageList, String newText, String model, UI ui, Span text, String uiContextKey) {
        text.add(message);
        return null;
    }

    @Override
    public String getAiResponseAsync(List<Message> messageList, String model, UI ui, Span text, String uiContextKey) {
        return message;
    }
    @Override
    public String getProviderName() {
        return "baidu";
    }

}
