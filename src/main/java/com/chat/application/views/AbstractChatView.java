package com.chat.application.views;

import com.chat.application.constant.ContextConst;
import com.chat.application.constant.ImageConst;
import com.chat.application.model.AiModel;
import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.model.Message;
import com.chat.application.service.ChatResponseMonitor;
import com.chat.application.util.ImageUtil;
import com.chat.application.util.RequestUtil;
import com.chat.application.views.message.MessageList;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
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
    public MessageList messageList = new MessageList();
    public TextField message = new TextField();

    public List<Message> context = new ArrayList<>();
    public Button sendButton;

    public AbstractChatView() {
        message.setPlaceholder("Enter a message... ");
        sendButton = new Button(VaadinIcon.ENTER.create(), buttonClickEvent -> {
            if (!StringUtils.isBlank(message.getValue())){
                sendMessage();
            }
        });
        sendButton.addClickShortcut(Key.ENTER);

        Button clearButton = new Button(VaadinIcon.TRASH.create(), buttonClickEvent -> {
            clearSession();
        });

        Button refreshButton = new Button(VaadinIcon.REFRESH.create(), buttonClickEvent -> {
            UI.getCurrent().getPage().reload();
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(message, sendButton,refreshButton, clearButton);
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
        /* 上下文最多保留15句 */
        if (this.context.size() > 20){
            List<Message> tmpMessages = this.context
                    .subList(this.context.size() - 4
                            , this.context.size());
            this.context = new ArrayList<>(tmpMessages);
        }
        /* 出现结果前禁用按钮 */
        sendButton.setEnabled(false);
        UI.getCurrent().push();
        /* 获取上下文 */
        String text = message.getValue();
        if (CollectionUtils.isEmpty(this.context)){
            this.context.add(new Message().setRole("user").setContent(getPrompt()));
            this.context.add(new Message().setRole("assistant").setContent("你好！请发一些消息吧"));
        }
        this.context.add(new Message().setRole("user").setContent(text));

        Avatar avatar = ImageUtil.getAvatar(ImageConst.ME);
        messageList.addMessage(
                ImageConst.ME.getName()
                , avatar
                , text
                , true);
        /* 获取结果 */
        message.clear();
        UI.getCurrent().push();

        AiModel model = getModel();
        Span text1 = messageList.addMessage(
                getCharacterName()
                , getAvatar()
                , ""
                , false);

        AsyncStatusInfo info = new AsyncStatusInfo().setMessageList(this.context)
                        .setNewText(text)
                        .setModelName(model.getModelName())
                        .setUi(UI.getCurrent())
                        .setText(text1)
                        .setUiContextKey(ContextConst.contextPrefix + getCharacterName())
                        .setButton(sendButton)
                        .setVaadinSession(VaadinSession.getCurrent())
                        .setIp(RequestUtil.getRequestIp())
                ;

        chatResponseMonitor
                .getChatResponseService(model.getProvider())
                .getChatResponseAsync(info);


    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UI.getCurrent().getPushConfiguration().setPushMode(PushMode.MANUAL);
        /* 设置UI过期时间为5分钟 */
        Optional.ofNullable(UI.getCurrent()
                        .getSession()
                        .getAttribute(ContextConst.contextPrefix + getCharacterName()))
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
        messageList.addMessage(
                getCharacterName()
                , getAvatar()
                , "你好！请发一些消息吧"
                , false);
    }

    private void clearSession(){
        messageList.setText("");
        context = new ArrayList<>();
        UI.getCurrent()
                .getSession()
                .setAttribute(ContextConst.contextPrefix + getCharacterName(),null);
        sendHello();
    }

}
