package com.chat.application.listener;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.OpenaiResponse;
import com.chat.application.service.UiService;
import com.chat.application.util.JsScriptUtil;
import com.chat.application.util.UiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OpenAiEventSourceListener extends EventSourceListener {

    private Boolean codeStart = false;

    private String codeCache = "";

    private String fullResult = "";

    private AsyncStatusInfo asyncStatusInfo;

    private Label label;

    private Button copyButton;

    private UiService uiService;

    public OpenAiEventSourceListener(AsyncStatusInfo asyncStatusInfo){
        this.asyncStatusInfo = asyncStatusInfo;
        this.uiService = asyncStatusInfo.getUiService();
    }
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (asyncStatusInfo.getIsCancelled().get()){
            if (!"[DONE]".equals(data)){
                String info = getContentFromData(data);
                if (StringUtils.isNotEmpty(info)) {
                    fullResult += info;
                }
            }else{
                asyncStatusInfo.getUi().accessSynchronously(() -> {
                    uiService.addToken(asyncStatusInfo, new ArrayList<>(asyncStatusInfo.getMessageList()){{
                        add(Message.builder()
                                .role(Message.Role.ASSISTANT)
                                .content(fullResult)
                                .build());
                    }});
                    asyncStatusInfo.getUi().push();
                });
            }
            return;
        }
        if (!"[DONE]".equals(data)){
            String info = getContentFromData(data);
            if (StringUtils.isNotEmpty(info)) {
                fullResult += info;
                if (info.trim().startsWith("``")){
                    if (codeStart){
                        asyncStatusInfo.getUi().accessSynchronously(() -> {
                            copyButton.setVisible(true);
                            UiUtil.addCopyButton(copyButton , codeCache);
                            asyncStatusInfo.getUi().push();
                        });
                        copyButton = null;
                        label = null;
                        codeStart = false;
                        codeCache = "";
                    }else{
                        codeStart = true;
                        asyncStatusInfo.getUi().accessSynchronously(() -> {
                            label = new Label();
                            copyButton = new Button("copy", VaadinIcon.COPY.create());
                            copyButton.setVisible(false);
                            asyncStatusInfo.getText().add(copyButton, label);
                            asyncStatusInfo.getUi().push();
                        });
                    }
                }else if (codeStart){
                    if (!info.startsWith("`\n") ){
                        asyncStatusInfo.getUi().access(() -> {
                            codeCache += info;
                            label.getElement().setProperty("innerHTML"
                                    , JsScriptUtil.getCodeContentScript(JsScriptUtil.codeTransfer(codeCache)));
                            UiUtil.scrollToBottomCheck(asyncStatusInfo);
                            asyncStatusInfo.getUi().push();
                        });
                    }
                }else{
                    asyncStatusInfo.getUi().accessSynchronously(() -> {
                                asyncStatusInfo.getText().add(info.contains("\n")
                                        ? new Html("<span>"
                                        + info.replaceAll("\n+","<br>")
                                        + "</span>")
                                        : new Span(info));
                                UiUtil.scrollToBottomCheck(asyncStatusInfo);
                                asyncStatusInfo.getUi().push();
                    });
                    /* 控制推送的速率 */
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }else{
            /* 执行结束 */
            asyncStatusInfo.getUi().access(() -> {
                uiService.updateCharacter(asyncStatusInfo, fullResult, Message.Role.ASSISTANT, true);
            });
        }
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response){
        asyncStatusInfo.getUi().access(() -> {
            asyncStatusInfo.getSendButton().setEnabled(true);
            asyncStatusInfo.getText().add("系统繁忙，请稍后再试");
            /* 清空记录 */
            asyncStatusInfo.getMessageList().clear();
            asyncStatusInfo.getUi().getSession()
                    .setAttribute(asyncStatusInfo.getUiContextKey(),null);

            asyncStatusInfo.getUi().push();
            asyncStatusInfo.setMessageList(new ArrayList<>());
        });
        log.error("OpenAI请求失败 [{}]",response, t);
        eventSource.cancel();
    }

    private String getContentFromData(String data){
        try {
            OpenaiResponse response = new ObjectMapper().readValue(data, OpenaiResponse.class);
            return response.getChoices()
                    .get(0)
                    .getDelta()
                    .getContent();
        }catch (Exception e){
            log.error("OpenAI响应消息解析失败:[{}]",data, e);
            throw new RuntimeException(e);
        }
    }
}
