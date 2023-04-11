package com.chat.application.service;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.Message;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;

import java.util.List;
import java.util.Map;

public interface ChatResponseService {

    String getChatResponseAsync(AsyncStatusInfo asyncStatusInfo);

    String getAiResponseAsync(AsyncStatusInfo asyncStatusInfo);

    String getProviderName();

}
