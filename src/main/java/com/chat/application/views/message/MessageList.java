package com.chat.application.views.message;

import com.chat.application.util.UiUtil;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;

public class MessageList extends Div {

    public MessageList() {
        setClassName(getClass().getSimpleName());
    }

    public Span addMessage(String from, Avatar avatar, String text, Boolean isCurrentUser){

        Span fromContainer = new Span(new Text(""));
        fromContainer.addClassName(getClass().getSimpleName() + "-name");

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


