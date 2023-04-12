package com.chat.application.util;

import com.chat.application.model.AsyncStatusInfo;
import com.unfbx.chatgpt.entity.chat.Message;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UiUtil {

    public static void updateCharacter(AsyncStatusInfo asyncStatusInfo, String fullContent, Message.Role role){
        asyncStatusInfo.getButton().setEnabled(true);
        asyncStatusInfo.getMessageList()
                .add(Message.builder()
                        .role(role)
                        .content(fullContent)
                        .build());
        asyncStatusInfo.getUi().getSession()
                .setAttribute(asyncStatusInfo.getUiContextKey()
                        , asyncStatusInfo.getMessageList());
        asyncStatusInfo.getUi().push();
        log.info("IP:[{}], 问题: [{}], 回答人: [{}], 答案: [{}]",asyncStatusInfo.getIp()
                , asyncStatusInfo.getMessageList()
                        .get(asyncStatusInfo.getMessageList().size() - 2)
                        .getContent()
                , asyncStatusInfo.getModel().getProvider()
                , fullContent);
    }

    public static Component[] parseCodeSegment(String text){
        List<Component> componentList = new ArrayList<>();
        String[] textFields = text.split("```");
        for(int i=0; i< textFields.length; i++){
            if (i%2 == 0){
                componentList.add(new Text(textFields[i]));
            }else{
                addCodeComponent(componentList, textFields[i]);
            }
        }
        return componentList.toArray(new Component[componentList.size()]);
    }

    public static void addCodeComponent(List<Component> componentList, String text){
        Label textLabel = new Label();
        String textContent = JsScriptUtil.codeTransfer(text);
        textLabel.getElement().setProperty("innerHTML",JsScriptUtil.getCodeContentScript(textContent));

        Button copyButton = new Button("copy", VaadinIcon.COPY.create());
        String copyContent = text;
        copyButton.addClickListener(event -> {
            UI.getCurrent().getPage().executeJs(JsScriptUtil.copyContentScript(),copyContent);
        });
        copyButton.getStyle().set("color","black");
        componentList.add(copyButton);
        componentList.add(textLabel);
    }


}
