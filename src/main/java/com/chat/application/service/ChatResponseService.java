package com.chat.application.service;

import com.chat.application.model.AsyncStatusInfo;

public interface ChatResponseService {

    void getChatResponseAsync(AsyncStatusInfo asyncStatusInfo);

    void getAiResponseAsync(AsyncStatusInfo asyncStatusInfo);

    String getProviderName();

}
