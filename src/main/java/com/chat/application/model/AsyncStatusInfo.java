package com.chat.application.model;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinSession;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AsyncStatusInfo {

    public List<Message> messageList;
    private String newText;
    private String modelName;

    private UI ui;

    private Span text;
    private String uiContextKey;

    private Button button;

    private VaadinSession vaadinSession;

}