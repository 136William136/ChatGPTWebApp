package com.chat.application.model;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AsyncStatusInfo {

    private List<Message> messageList;
    private String newText;
    private String modelName;

    private UI ui;

    private Span text;
    private String uiContextKey;

}
