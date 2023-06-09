package com.chat.application.service.impl;

import com.chat.application.listener.OpenAiEventSourceListener;
import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.service.AbstractChatResponseService;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("openAiResponseServiceImpl")
@Slf4j
public class OpenAiResponseServiceImpl extends AbstractChatResponseService {
    @Value("${service.key.openai}")
    private String openaiKey;
    @Getter
    private String providerName = "openai";
    @Override
    public void getAiResponseAsync(AsyncStatusInfo asyncStatusInfo){
        OpenAiStreamClient client = OpenAiStreamClient.builder()
                .apiKey(Arrays.asList(openaiKey))
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(new OkHttpClient.Builder().pingInterval(10, TimeUnit.SECONDS).build())
                .build();
        OpenAiEventSourceListener eventSourceListener = new OpenAiEventSourceListener(asyncStatusInfo);
        /* 上下文最多保留9句 */
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(subMessageList(9, asyncStatusInfo.getMessageList()))
                .model(asyncStatusInfo.getModel().getModelName())
                .build();
        client.streamChatCompletion(chatCompletion, eventSourceListener);
    }

    private List<Message> subMessageList(int sizeLimit, List<Message> messageList){
        if (messageList.size() > sizeLimit){
            List<Message> tmpMessages = messageList
                    .subList(messageList.size() - sizeLimit
                            , messageList.size());
            return new ArrayList<>(tmpMessages);
        }
        return messageList;
    }

}
