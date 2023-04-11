package com.chat.application.listener;

import com.chat.application.constant.ContextConst;
import com.chat.application.model.Message;
import com.chat.application.model.OpenaiResponse;
import com.chat.application.util.JsScriptUtil;
import com.chat.application.util.RequestUtil;
import com.chat.application.views.message.MessageList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class AiEventSourceListener extends EventSourceListener {

    private UI ui;

    private Span text;

    private Boolean codeStart = false;

    private String codeCache = "";

    private String fullResult = "";

    private List<Message> messageList;

    private String uiContextKey;

    public AiEventSourceListener(UI ui, Span text, List<Message> messageList, String uiContextKey){
        this.ui = ui;
        this.text = text;
        this.messageList = messageList;
        this.uiContextKey = uiContextKey;
    }
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (!data.equals("[DONE]")){
            try {

                OpenaiResponse response = new ObjectMapper().readValue(data, OpenaiResponse.class);
                String info = response.getChoices()
                        .get(0)
                        .getDelta()
                        .getContent();
                //log.info("返回数据: [{}]",info);
                if (StringUtils.isNotEmpty(info)) {
                    fullResult += info;
                    if (info.equals("```") && !codeStart){
                        codeStart = true;
                    }else if (info.equals("``") && codeStart){
                        codeStart = false;
                        String copyContent = new String(codeCache);
                        Label textLabel = new Label();
                        String textContent = JsScriptUtil.codeTransfer(copyContent);
                        textLabel.getElement().setProperty("innerHTML",JsScriptUtil.getCodeContentScript(textContent));

                        Button copyButton = new Button("", VaadinIcon.COPY.create());
                        copyButton.addClickListener(event -> {
                            UI.getCurrent().getPage().executeJs(JsScriptUtil.copyContentScript(),copyContent);
                        });
                        copyButton.getStyle().set("color","black");
                        text.add(copyButton, textLabel);
                        ui.push();
                        codeCache = "";
                    }else if (codeStart){
                        codeCache += info;
                    }else{
                        text.add(info);
                        ui.push();
                    }
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        }else{
            /* 执行结束 */
            Message question = messageList.get(messageList.size()-1);
            messageList.add(new Message().setRole("assistant").setContent(fullResult));
            ui.getSession().setAttribute(uiContextKey,messageList);


            log.info("问题: [{}], 答案: [{}]",question.getContent(), fullResult );
            /* 上下文最多保留10句 */
            messageList = messageList.size() > 20
                    ? messageList.subList(messageList.size() - 20, messageList.size())
                    : messageList;
        }
    }

}
