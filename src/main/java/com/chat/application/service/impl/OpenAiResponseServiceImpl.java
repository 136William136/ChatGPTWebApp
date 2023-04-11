package com.chat.application.service.impl;

import com.chat.application.listener.AiEventSourceListener;
import com.chat.application.model.Message;
import com.chat.application.service.AbstractChatResponseService;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("openAiResponseServiceImpl")
@Slf4j
public class OpenAiResponseServiceImpl extends AbstractChatResponseService {
    @Value("${service.key.openai}")
    private String openaiKey;
    @Override
    public String getAiResponseAsync(List<Message> messageList
            , String model, UI ui, Span text, String uiContextKey){
        OpenAiStreamClient client = OpenAiStreamClient.builder()
                .apiKey(Arrays.asList(openaiKey))
                .keyStrategy(new KeyRandomStrategy())
                .build();
        //聊天模型：gpt-3.5
        List<com.unfbx.chatgpt.entity.chat.Message> messageList1 = messageList.stream().map(message -> {
            com.unfbx.chatgpt.entity.chat.Message.Role role = com.unfbx.chatgpt.entity.chat.Message.Role.USER;
            if (message.getRole().equalsIgnoreCase("assistant")){
                role = com.unfbx.chatgpt.entity.chat.Message.Role.ASSISTANT;
            }
            return com.unfbx.chatgpt.entity.chat.Message.builder()
                    .role(role).content(message.getContent()).build();
        }).collect(Collectors.toList());
        AiEventSourceListener eventSourceListener = new AiEventSourceListener(ui, text, messageList, uiContextKey);
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messageList1).build();
        client.streamChatCompletion(chatCompletion, eventSourceListener);
        return "";
    }

    @Override
    public String getProviderName() {
        return "openai";
    }

}
