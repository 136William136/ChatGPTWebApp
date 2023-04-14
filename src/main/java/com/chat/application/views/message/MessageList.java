package com.chat.application.views.message;

import com.chat.application.util.JsScriptUtil;
import com.chat.application.util.UiUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ScrollOptions;
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

import javax.swing.plaf.basic.BasicSliderUI;

public class MessageList extends Div {

    public MessageList() {
        setClassName(getClass().getSimpleName());
    }

    public Span addMessage(String from, Avatar avatar, String text, Boolean isCurrentUser){

        //Span fromContainer = new Span(new Text(from));
        Span fromContainer = new Span(new Text(""));
        fromContainer.addClassName(getClass().getSimpleName() + "-name");

        //Span text1 = new Span(text);
        Span text1 = new Span(UiUtil.parseCodeSegment(text));
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
        add(scroller);
        line.scrollIntoView(new ScrollOptions(ScrollOptions.Behavior.SMOOTH));
        return text1;
    }

}


