package com.chat.application.util;

import com.chat.application.model.AsyncStatusInfo;
import com.unfbx.chatgpt.entity.chat.Message;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public class UiUtil {

    public static Component[] parseCodeSegment(String text){
        List<Component> componentList = new ArrayList<>();
        String[] textFields = text.split("```");
        for(int i=0; i< textFields.length; i++){
            if (i%2 == 0){
                componentList.add(textFields[i].contains("\n")
                        ? new Html("<span>" + textFields[i]
                        .replaceAll("\n+", "<br>")
                        + "</span>")
                        : new Text(textFields[i]));
            }else{
                addCodeComponent(componentList, textFields[i]);
            }
        }
        return componentList.toArray(new Component[componentList.size()]);
    }

    private static void addCodeComponent(List<Component> componentList, String text){
        Label textLabel = new Label();
        String textContent = JsScriptUtil.codeTransfer(text);
        textLabel.getElement().setProperty("innerHTML",JsScriptUtil.getCodeContentScript(textContent));
        Button copyButton = new Button("copy", VaadinIcon.COPY.create());
        addCopyButton(copyButton , text);
        componentList.add(copyButton);
        componentList.add(textLabel);
    }
    public static void scrollToBottomCheck(AsyncStatusInfo asyncStatusInfo){
        if (asyncStatusInfo.getStayBottom().get()){
            asyncStatusInfo.getUi()
                    .getPage()
                    .executeJs(JsScriptUtil.scrollToBottom());
        }
    }


    public static void addCopyButton(Button copyButton , String text){
        String copyContent = text;
        copyButton.addClickListener(event -> {
            UI.getCurrent().getPage().executeJs(JsScriptUtil.copyContentScript(),copyContent);
        });
        copyButton.addClassName("copy-button");
    }


}
