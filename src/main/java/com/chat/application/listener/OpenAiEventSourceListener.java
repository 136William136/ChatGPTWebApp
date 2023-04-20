package com.chat.application.listener;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.OpenaiResponse;
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

@Slf4j
public class OpenAiEventSourceListener extends EventSourceListener {

    private Boolean codeStart = false;

    private String codeCache = "";

    private String fullResult = "";

    private AsyncStatusInfo asyncStatusInfo;

    private Label label;

    private Button copyButton;

    public OpenAiEventSourceListener(AsyncStatusInfo asyncStatusInfo){
        this.asyncStatusInfo = asyncStatusInfo;
    }
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (asyncStatusInfo.getIsCancelled().get()){
            return;
        }
        if (!"[DONE]".equals(data)){
            try {
                OpenaiResponse response = new ObjectMapper().readValue(data, OpenaiResponse.class);
                String info = response.getChoices()
                        .get(0)
                        .getDelta()
                        .getContent();
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
                                asyncStatusInfo.getText().add(copyButton);
                                asyncStatusInfo.getText().add(label);
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
                        Thread.sleep(50);
                    }
                }
            } catch (Exception e) {
                log.error("OpenAI响应消息解析失败",e);
            }
        }else{
            /* 执行结束 */
            asyncStatusInfo.getUi().access(() -> {
                UiUtil.updateCharacter(asyncStatusInfo, fullResult, Message.Role.ASSISTANT);
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
}
