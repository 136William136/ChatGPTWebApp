package com.chat.application.views.message;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;

import java.util.ArrayList;
import java.util.List;

public class MessageList extends Div {

    public MessageList() {
        setClassName(getClass().getSimpleName());
    }

    public void addMessage(String from, Avatar avatar, String text, Boolean isCurrentUser){

        Span fromContainer = new Span(new Text(from));
        fromContainer.addClassName(getClass().getSimpleName() + "-name");

        Div textContainer = getTextField(text);
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

    }

    public Div getTextField(String text){
        //Div textContainer = new Div(new Text(text));

        List<Component> componentList = new ArrayList<>();
        String[] textFields = text.split("```");
        if (textFields.length % 2 == 0){
            return new Div(new Text(text));
        }
        for(int i=0; i< textFields.length; i++){
            if (i%2 == 0){
                componentList.add(new Text(textFields[i]));
            }else{
                Label textLabel = new Label();
                String textContent = textFields[i];
                if (textContent.startsWith("html")){
                    textContent = textContent.replaceAll("\"","&quot")
                            .replaceAll("&","&amp;")
                            .replaceAll("<","&lt;")
                            .replaceAll(">","&gt;");
                }
                String backgroundColor = "#C0C0C0";
                String content = "<pre style='background-color:"+backgroundColor+"'>" +
                        "<code style='background-color:"+backgroundColor+"'>" + textContent + "</code></pre>";
                textLabel.getElement().setProperty("innerHTML",content);
                componentList.add(textLabel);
            }
        }
        return new Div(componentList.toArray(new Component[componentList.size()]));
    }

}
