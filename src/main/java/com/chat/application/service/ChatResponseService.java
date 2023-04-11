package com.chat.application.service;

import com.chat.application.model.AsyncStatusInfo;

public interface ChatResponseService {

    String getChatResponseAsync(AsyncStatusInfo asyncStatusInfo);

    String getAiResponseAsync(AsyncStatusInfo asyncStatusInfo);

    String getProviderName();

}
