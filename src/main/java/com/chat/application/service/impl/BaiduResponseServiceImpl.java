package com.chat.application.service.impl;

import com.chat.application.model.Message;
import com.chat.application.service.AbstractChatResponseService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("baiduResponseServiceImpl")
public class BaiduResponseServiceImpl extends AbstractChatResponseService {

    @Override
    public String getAiResponse(List<Message> messageList, String model) {
        return "百度AI尚未引入";
    }
    @Override
    public String getProviderName() {
        return "baidu";
    }

}
