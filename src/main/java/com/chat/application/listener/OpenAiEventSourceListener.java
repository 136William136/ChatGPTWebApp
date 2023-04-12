package com.chat.application.listener;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.OpenaiResponse;
import com.chat.application.util.JsScriptUtil;
import com.chat.application.util.UiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class OpenAiEventSourceListener extends EventSourceListener {

    private Boolean codeStart = false;

    private String codeCache = "";

    private String fullResult = "";

    private AsyncStatusInfo asyncStatusInfo;

    public OpenAiEventSourceListener(AsyncStatusInfo asyncStatusInfo){
        this.asyncStatusInfo = asyncStatusInfo;
    }
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        asyncStatusInfo.getVaadinSession().getLockInstance().lock();
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
                    if (info.startsWith("``") && !codeStart){
                        codeStart = true;
                    }else if (info.startsWith("``") && codeStart){
                        codeStart = false;
                        String copyContent = new String(codeCache);
                        Label textLabel = new Label();
                        String textContent = JsScriptUtil.codeTransfer(copyContent);
                        textLabel.getElement().setProperty("innerHTML",JsScriptUtil.getCodeContentScript(textContent));

                        Button copyButton = new Button("copy", VaadinIcon.COPY.create());
                        copyButton.addClickListener(event -> {
                            UI.getCurrent().getPage().executeJs(JsScriptUtil.copyContentScript(),copyContent);
                        });
                        copyButton.getStyle().set("color","black");
                        asyncStatusInfo.getText().add(copyButton, textLabel);
                        asyncStatusInfo.getUi().push();
                        codeCache = "";
                    }else if (codeStart){
                        codeCache += info;
                    }else{
                        asyncStatusInfo.getText().add(info);
                        asyncStatusInfo.getUi().push();
                    }
                }
            } catch (Exception e) {
                log.error("OpenAI响应消息解析失败",e);
            }
        }else{
            /* 执行结束 */
            UiUtil.updateCharacter(asyncStatusInfo, fullResult, Message.Role.ASSISTANT);
        }
        asyncStatusInfo.getVaadinSession().getLockInstance().unlock();
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response){
        asyncStatusInfo.getVaadinSession().getLockInstance().lock();
        asyncStatusInfo.getButton().setEnabled(true);
        asyncStatusInfo.getText().add("系统繁忙，请稍后再试；可以尝试清空消息或刷新页面");
        asyncStatusInfo.getUi().push();
        asyncStatusInfo.getVaadinSession().getLockInstance().unlock();
        log.error("OpenAI请求失败 [{}]",response, t);
        eventSource.cancel();
    }

}
