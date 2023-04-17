package com.chat.application.views.chat;

import com.chat.application.constant.ImageConst;
import com.chat.application.model.AiModel;
import com.chat.application.util.ImageUtil;
import com.chat.application.views.AbstractChatView;
import com.chat.application.views.MainLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@PageTitle("Room - 3.5")
@Route(value = "chatroom1", layout = MainLayout.class)
@RouteAlias(value = "",layout = MainLayout.class)
@Slf4j
public class ChatRoom1 extends AbstractChatView {

    @Value("${service.prompt.ChatRoom1}")
    private String prompt;

    @Value("${service.model.ChatRoom1}")
    private String model;

    @Override
    public String getPrompt(){return prompt;}

    @Override
    public AiModel getModel() {
        String provider = model.split(":")[0];
        String modelName = model.split(":")[1];
        return new AiModel().setModelName(modelName).setProvider(provider);
    }

    @Override
    public Avatar getAvatar() {
        return ImageUtil.getAvatar(ImageConst.CHATROOM1);
    }

    @Override
    public String getCharacterName(){
        return ImageConst.CHATROOM1.getName();
    }

}
