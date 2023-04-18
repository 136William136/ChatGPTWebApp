package com.chat.application.views.chat;

import com.chat.application.constant.ImageConst;
import com.chat.application.model.AiModel;
import com.chat.application.util.ImageUtil;
import com.chat.application.views.AbstractChatView;
import com.chat.application.views.MainLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@PageTitle("Room - 4.0")
@Route(value = "ChatRoom2", layout = MainLayout.class)
@RouteAlias(value = "phil",layout = MainLayout.class)
@Slf4j
public class ChatRoom2 extends AbstractChatView {
    @Value("${service.prompt.ChatRoom2}")
    @Getter
    private String prompt;

    @Value("${service.model.ChatRoom2}")
    private String model;

    @Override
    public AiModel getModel() {
        String provider = model.split(":")[0];
        String modelName = model.split(":")[1];
        return new AiModel().setModelName(modelName).setProvider(provider);
    }
    @Override
    public Avatar getAvatar() {
        return ImageUtil.getAvatar(ImageConst.CHATROOM2);
    }

    @Override
    public String getCharacterName(){
        return ImageConst.CHATROOM2.getName();
    }

}
