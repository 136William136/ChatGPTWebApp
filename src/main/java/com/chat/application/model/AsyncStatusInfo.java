package com.chat.application.model;

import com.unfbx.chatgpt.entity.chat.Message;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinSession;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Accessors(chain = true)
public class AsyncStatusInfo {

    private List<Message> messageList;
    private String newText;
    private AiModel model;

    private UI ui;

    private Span text;
    private String uiContextKey;

    private Button sendButton;

    private AtomicBoolean stayBottom;

    private VaadinSession vaadinSession;

    private String ip;

}
