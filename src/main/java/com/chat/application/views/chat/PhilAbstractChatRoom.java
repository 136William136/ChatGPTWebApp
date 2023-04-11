package com.chat.application.views.chat;

import com.chat.application.constant.ImageConst;
import com.chat.application.model.AiModel;
import com.chat.application.util.ImageUtil;
import com.chat.application.views.AbstractChatView;
import com.chat.application.views.MainLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@PageTitle("聊天室2号")
@Route(value = "philChatRoom", layout = MainLayout.class)
@RouteAlias(value = "phil",layout = MainLayout.class)
@Slf4j
public class PhilAbstractChatRoom extends AbstractChatView {
    @Value("${service.prompt.philChatRoom}")
    private String prompt;

    @Value("${service.model.philChatRoom}")
    private String model;

    @Override
    public AiModel getModel() {
        String provider = model.split(":")[0];
        String modelName = model.split(":")[1];
        return new AiModel().setModelName(modelName).setProvider(provider);
    }
    @Override
    public String getPrompt(){
        return prompt;
    }
    @Override
    public Avatar getAvatar() {
        return ImageUtil.getAvatar(ImageConst.PHILCHATROOM);
    }

    @Override
    public String getCharacterName(){
        return ImageConst.PHILCHATROOM.getName();
    }

}
