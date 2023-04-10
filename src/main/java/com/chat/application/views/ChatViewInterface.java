package com.chat.application.views;

import com.chat.application.model.AiModel;
import com.vaadin.flow.component.avatar.Avatar;

import java.util.Map;

public interface ChatViewInterface {

    void sendMessage();

    Avatar getAvatar();

    String getCharacterName();

    String getPrompt();

    AiModel getModel();

}
