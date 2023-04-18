package com.chat.application.listener;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.OpenaiResponse;
import com.chat.application.util.JsScriptUtil;
import com.chat.application.util.UiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.progressbar.ProgressBar;
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

    public OpenAiEventSourceListener(AsyncStatusInfo asyncStatusInfo){
        this.asyncStatusInfo = asyncStatusInfo;
    }
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        //asyncStatusInfo.getVaadinSession().getLockInstance().lock();
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
                                List<Component> componentList = new ArrayList<>();
                                copyButton.setVisible(true);
                                UiUtil.addCopyButton(copyButton, componentList, codeCache);
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
                                String codeContent = codeCache.startsWith("html")
                                        ? codeCache.replaceAll("<","&lt;")
                                        .replaceAll(">","&gt;") : codeCache;
                                label.getElement().setProperty("innerHTML"
                                        , JsScriptUtil.getCodeContentScript(codeContent));
                                UiUtil.scrollToBottomCheck(asyncStatusInfo);
                                asyncStatusInfo.getUi().push();
                            });
                        }
                    }else{
                        asyncStatusInfo.getUi().access(() -> {
                                    if (info.contains("\n")){
                                        Html text = new Html("<span>"
                                                +info.replaceAll("\n+","<br>")
                                                + "</span>");
                                        asyncStatusInfo.getText().add(text);
                                        UiUtil.scrollToBottomCheck(asyncStatusInfo, text);
                                    }else{
                                        Span text = new Span(info);
                                        asyncStatusInfo.getText().add(text);
                                        UiUtil.scrollToBottomCheck(asyncStatusInfo, text);
                                    }
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
        //asyncStatusInfo.getVaadinSession().getLockInstance().unlock();
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response){
        //asyncStatusInfo.getVaadinSession().getLockInstance().lock();
        asyncStatusInfo.getSendButton().setEnabled(true);
        asyncStatusInfo.getText().add("系统繁忙，请稍后再试");
        /* 清空记录 */
        asyncStatusInfo.getMessageList().clear();
        asyncStatusInfo.getUi().getSession()
                .setAttribute(asyncStatusInfo.getUiContextKey(),null);

        asyncStatusInfo.getUi().push();
        asyncStatusInfo.setMessageList(new ArrayList<>());
        //asyncStatusInfo.getVaadinSession().getLockInstance().unlock();
        log.error("OpenAI请求失败 [{}]",response, t);
        eventSource.cancel();
    }
}
