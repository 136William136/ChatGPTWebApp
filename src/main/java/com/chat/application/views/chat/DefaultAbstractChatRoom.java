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
@Route(value = "chatroom", layout = MainLayout.class)
@RouteAlias(value = "",layout = MainLayout.class)
@Slf4j
public class DefaultAbstractChatRoom extends AbstractChatView {

    @Value("${service.prompt.defaultChatRoom}")
    private String prompt;

    @Value("${service.model.defaultChatRoom}")
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
        return ImageUtil.getAvatar(ImageConst.DEFAULTCHATROOM);
    }

    @Override
    public String getCharacterName(){
        return ImageConst.DEFAULTCHATROOM.getName();
    }

}
