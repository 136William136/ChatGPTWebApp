package com.chat.application.listener;

import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.Message;
import com.chat.application.model.OpenaiResponse;
import com.chat.application.util.JsScriptUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.UI;
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

                        Button copyButton = new Button("", VaadinIcon.COPY.create());
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
            try {
                /* 执行结束 */
                Message question = asyncStatusInfo.getMessageList()
                        .get(asyncStatusInfo.getMessageList().size() - 1);
                asyncStatusInfo.getMessageList()
                        .add(new Message().setRole("assistant").setContent(fullResult));
                asyncStatusInfo.getUi().getSession()
                        .setAttribute(asyncStatusInfo.getUiContextKey()
                                , asyncStatusInfo.getMessageList());

                log.info("IP:[{}], 问题: [{}], 答案: [{}]",asyncStatusInfo.getIp()
                        , question.getContent()
                        , fullResult);
            }catch (Exception e){
                log.error("OpenAI写入结果异常 ",e);
            }finally {
                asyncStatusInfo.getButton().setEnabled(true);
                asyncStatusInfo.getUi().push();
            }
        }
        asyncStatusInfo.getVaadinSession().getLockInstance().unlock();
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response){
        asyncStatusInfo.getVaadinSession().getLockInstance().lock();

        asyncStatusInfo.getButton().setEnabled(true);
        asyncStatusInfo.getUi().push();
        log.error("OpenAI请求失败 ",t);
        asyncStatusInfo.getText().add("系统繁忙，请稍后再试");
        asyncStatusInfo.getUi().push();

        asyncStatusInfo.getVaadinSession().getLockInstance().unlock();
    }

}
