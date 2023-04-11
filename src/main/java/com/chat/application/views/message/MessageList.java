package com.chat.application.views.message;

import com.chat.application.util.JsScriptUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.page.Page;

import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.List;

public class MessageList extends Div {

    public MessageList() {
        setClassName(getClass().getSimpleName());
    }

    public Span addMessage(String from, Avatar avatar, String text, Boolean isCurrentUser){

        Span fromContainer = new Span(new Text(from));
        //Span fromContainer = new Span(new Text(""));
        fromContainer.addClassName(getClass().getSimpleName() + "-name");

        //Div textContainer = getTextField(text);
        Span text1 = new Span(text);
        Div textContainer = new Div(text1);
        textContainer.addClassName(getClass().getSimpleName() + "-bubble");

        Div avatarContainer = new Div(avatar, fromContainer);
        avatarContainer.addClassName(getClass().getSimpleName() + "-avatar");

        Div line = new Div(avatarContainer, textContainer);
        if (isCurrentUser) {
            line.addClassName(getClass().getSimpleName() + "-row-currentUser");
            textContainer.addClassName(getClass().getSimpleName() + "-bubble-currentUser");
        }else{
            line.addClassName(getClass().getSimpleName() + "-row");
        }
        Scroller scroller = new Scroller(line);
        scroller.getStyle().set("overflow-x","hidden");
        scroller.getStyle().set("overflow","hidden");
        add(scroller);
        line.getElement().callJsFunction("scrollIntoView");
        return text1;
    }

    public static Div getTextField(String text){
        List<Component> componentList = new ArrayList<>();
        String[] textFields = text.split("```");
        for(int i=0; i< textFields.length; i++){
            if (i%2 == 0){
                componentList.add(new Text(textFields[i]));
            }else{
                Label textLabel = new Label();
                String textContent = JsScriptUtil.codeTransfer(textFields[i]);
                textLabel.getElement().setProperty("innerHTML",JsScriptUtil.getCodeContentScript(textContent));

                Button copyButton = new Button("", VaadinIcon.COPY.create());
                String copyContent = textFields[i];
                copyButton.addClickListener(event -> {
                    UI.getCurrent().getPage().executeJs(JsScriptUtil.copyContentScript(),copyContent);
                });
                copyButton.getStyle().set("color","black");
                componentList.add(copyButton);
                componentList.add(textLabel);
            }
        }
        return new Div(componentList.toArray(new Component[componentList.size()]));

    }

}


