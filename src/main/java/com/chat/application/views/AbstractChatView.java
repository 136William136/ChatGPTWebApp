package com.chat.application.views;

import com.chat.application.constant.ImageConst;
import com.chat.application.model.AiModel;
import com.chat.application.model.Message;
import com.chat.application.service.ChatResponseMonitor;
import com.chat.application.util.ImageUtil;
import com.chat.application.util.RequestUtil;
import com.chat.application.views.message.MessageList;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.communication.PushMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
public abstract class AbstractChatView extends VerticalLayout implements ChatViewInterface
        , BeforeEnterObserver
        //, BeforeLeaveObserver
{
    @Autowired
    private ChatResponseMonitor chatResponseMonitor;
    private final String contextPrefix = "context:";
    public MessageList messageList = new MessageList();
    public TextField message = new TextField();

    public List<Message> context = new ArrayList<>();

    public AbstractChatView() {
        message.setPlaceholder("Enter a message... ");
        Button sendButton = new Button(VaadinIcon.ENTER.create(), buttonClickEvent -> {
            if (!StringUtils.isBlank(message.getValue())){
                sendMessage();
            }
        });
        sendButton.addClickShortcut(Key.ENTER);

        Button clearButton = new Button(VaadinIcon.TRASH.create(), buttonClickEvent -> {
            clearSession();
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(message, sendButton, clearButton);
        horizontalLayout.setWidth("80%");
        horizontalLayout.setMargin(true);
        horizontalLayout.setSpacing(true);
        horizontalLayout.expand(message);
        horizontalLayout.getStyle().set("opacity","0.8");
        horizontalLayout.getStyle().set("background-color","white");
        horizontalLayout.getStyle().set("position","absolute");
        horizontalLayout.getStyle().set("bottom","5%");
        add(messageList, horizontalLayout);
    }

    @Override
    public void sendMessage(){
        /* 获取上下文 */
        String text = message.getValue();
        List<Message> currentMessage = new ArrayList<>();
        if (CollectionUtils.isEmpty(this.context)){
            currentMessage.add(new Message().setRole("user").setContent(getPrompt()));
            currentMessage.add(new Message().setRole("assistant").setContent("你好！请发一些消息吧"));
        }else{
            currentMessage = this.context;
        }
        currentMessage.add(new Message().setRole("user").setContent(text));

        Avatar avatar = ImageUtil.getAvatar(ImageConst.ME);
        messageList.addMessage(ImageConst.ME.getName()
                , avatar
                , text
                , true);
        /* 获取结果 */
        message.clear();
        UI.getCurrent().push();

        AiModel model = getModel();
        String answer = chatResponseMonitor
                .getChatResponseService(model.getProvider())
                .getChatResponse(currentMessage
                        , text, model.getModelName());
        /* 添加上下文 */
        currentMessage.add(new Message()
                .setRole("assistant")
                .setContent(answer));

        /* 上下文最多保留8句 */
        currentMessage = currentMessage.size() > 8
                ? currentMessage.subList(currentMessage.size() - 8, currentMessage.size())
                : currentMessage;
        this.context = new ArrayList<>(currentMessage);
        messageList.addMessage(getCharacterName()
                , getAvatar()
                , answer
                , false);
        log.info("IP: [{}], 问题: [{}], 回答完毕: [{}]", RequestUtil.getRequestIp(), text, answer);

        saveSession();
    }

    private void saveSession(){
        UI.getCurrent()
                .getSession()
                .setAttribute(contextPrefix + getCharacterName(),this.context);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UI.getCurrent().getPushConfiguration().setPushMode(PushMode.MANUAL);
        /* 设置UI过期时间为5分钟 */
//        UI.getCurrent().setPollInterval(300);
        Optional.ofNullable(UI.getCurrent()
                        .getSession()
                        .getAttribute(contextPrefix + getCharacterName()))
                .ifPresentOrElse(historyContext -> {
                    context = (List<Message>) historyContext;
                    if (CollectionUtils.isEmpty(context)){
                        sendHello();
                    }
                    context.stream().skip(1).forEach(oldMessage -> {
                        String from = "user".equalsIgnoreCase(oldMessage.getRole())
                                ? ImageConst.ME.getName()
                                : getCharacterName();
                        Avatar avatar = "user".equalsIgnoreCase(oldMessage.getRole())
                                ? ImageUtil.getAvatar(ImageConst.ME)
                                : getAvatar();
                        Boolean isCurrent = "user".equalsIgnoreCase(oldMessage.getRole()) ? true : false;
                        messageList.addMessage(from,avatar
                                , oldMessage.getContent()
                                , isCurrent);
                    });
                }, () -> sendHello());
    }
    private void sendHello(){
        messageList.addMessage(getCharacterName()
                , getAvatar()
                , "你好！请发一些消息吧"
                , false);
    }

    private void clearSession(){
        messageList.setText("");
        context = new ArrayList<>();
        UI.getCurrent()
                .getSession()
                .setAttribute(contextPrefix + getCharacterName(),null);
        sendHello();
    }

}
