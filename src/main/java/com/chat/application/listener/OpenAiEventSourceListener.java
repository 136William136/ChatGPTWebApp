package com.chat.application.listener;

import com.chat.application.constant.ElementConst;
import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.OpenaiResponse;
import com.chat.application.util.UiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
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

    private ProgressBar spinner = new ProgressBar();

    private AsyncStatusInfo asyncStatusInfo;

    public OpenAiEventSourceListener(AsyncStatusInfo asyncStatusInfo){
        this.asyncStatusInfo = asyncStatusInfo;
        spinner.setIndeterminate(true);
        spinner.setVisible(true);
    }
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        //asyncStatusInfo.getVaadinSession().getLockInstance().lock();
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
                        asyncStatusInfo.getUi().accessSynchronously(() -> {
                            asyncStatusInfo.getText().add(spinner);
                            asyncStatusInfo.getUi().push();
                        });
                    }else if (info.startsWith("``") && codeStart){
                        codeStart = false;
                        List<Component> componentList = new ArrayList<>();
                        UiUtil.addCodeComponent(componentList, new String(codeCache));
                        asyncStatusInfo.getUi().access(() -> {
                            asyncStatusInfo.getText().remove(spinner);
                            asyncStatusInfo.getText()
                                    .add(componentList
                                            .toArray(new Component[componentList.size()]));
                            UiUtil.scrollToBottomCheck(asyncStatusInfo);
                            asyncStatusInfo.getUi().push();
                        });
                        codeCache = "";
                    }else if (codeStart){
                        if (!info.startsWith("`\n")) {
                            codeCache += info;
                        }
                    }else{
                        asyncStatusInfo.getUi().access(() -> {
                                    Span text = new Span(info);
                                    asyncStatusInfo.getText().add(text);
                                    UiUtil.scrollToBottomCheck(asyncStatusInfo, text);
                                    asyncStatusInfo.getUi().push();
                        });
                        /* 控制推送的速率 */
                        Thread.sleep(100);
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
