package com.chat.application.views;

import com.chat.application.constant.ContextConst;
import com.chat.application.constant.ElementConst;
import com.chat.application.constant.ImageConst;
import com.chat.application.model.AiModel;
import com.chat.application.model.AsyncStatusInfo;
import com.chat.application.service.ChatResponseMonitor;
import com.chat.application.util.ImageUtil;
import com.chat.application.util.RequestUtil;
import com.chat.application.views.message.MessageList;
import com.unfbx.chatgpt.entity.chat.Message;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public Button bottomButton;
    public volatile AtomicBoolean stayBottom = new AtomicBoolean(false);

    public AbstractChatView() {
        message.setPlaceholder("Enter a message... ");
        sendButton = new Button(VaadinIcon.ENTER.create(), buttonClickEvent -> {
            if (!StringUtils.isBlank(message.getValue())){
                sendMessage();
            }
        });
        sendButton.addClickShortcut(Key.ENTER);
        sendButton.setTooltipText("发送");

        Button clearButton = new Button(VaadinIcon.TRASH.create(), buttonClickEvent -> {
            clearSession();
        });
        clearButton.setTooltipText("清空");

        Button refreshButton = new Button(VaadinIcon.REFRESH.create(), buttonClickEvent -> {
            UI.getCurrent().getPage().reload();
        });
        refreshButton.setTooltipText("刷新");

        bottomButton = new Button(VaadinIcon.ARROW_DOWN.create());
        bottomButton.addClickListener(buttonClickEvent -> {
            UI.getCurrent().accessSynchronously(() -> {
                if (stayBottom.get()) {
                    stayBottom.set(false);
                    bottomButton.removeClassName("selected");
                }else{
                    stayBottom.set(true);
                    bottomButton.addClassName("selected");
                    if (messageList.getComponentCount() > 0) {
                        messageList.getComponentAt(messageList.getComponentCount() - 1)
                                .getElement().scrollIntoView(ElementConst.SmoothScroll);
                    }
                }
                UI.getCurrent().push();
            });
        });

        bottomButton.addClickShortcut(Key.ARROW_DOWN);
        bottomButton.setTooltipText("置底");

        Button revertButton = new Button(VaadinIcon.ROTATE_LEFT.create(), buttonClickEvent -> {
            if (messageList.getComponentCount() >= 2 && context.size() >= 2) {
                messageList.remove(messageList.getComponentAt(messageList.getComponentCount()-1));
                messageList.remove(messageList.getComponentAt(messageList.getComponentCount()-1));
                sendButton.setEnabled(true);

                context = new ArrayList<>(context.subList(0
                        , context.get(context.size()-1).getRole().equalsIgnoreCase("user")
                                ? context.size()-1
                                : context.size()-2));
                UI.getCurrent()
                        .getSession()
                        .setAttribute(ContextConst.contextPrefix + getCharacterName(),context);
                UI.getCurrent().push();
            }
        });
        revertButton.setTooltipText("撤回");

        HorizontalLayout horizontalLayout = new HorizontalLayout(
                refreshButton
                , message
                , sendButton
                , bottomButton
                , revertButton
                , clearButton);
        horizontalLayout.setMargin(true);
        horizontalLayout.setSpacing(true);
        horizontalLayout.expand(message);
        horizontalLayout.addClassName("bottom-message");
        add(messageList, horizontalLayout);

        /* 移除掉底部的navbarBottom 在移动设备中会显示 */
        UI.getCurrent().getPage().executeJs("document.getElementsByTagName(\"vaadin-app-layout\")[0].shadowRoot.getElementById(\"navbarBottom\").remove()");
    }

    @Override
    public void sendMessage(){
        /* 出现结果前禁用按钮 */
        sendButton.setEnabled(false);
        UI.getCurrent().push();

        /* 上下文最多保留15句 */
        checkMessageListSize();
        /* 获取上下文 */
        String text = message.getValue();
        if (CollectionUtils.isEmpty(this.context)){
            this.context.add(Message.builder().role(Message.Role.USER).content(getPrompt()).build());
            this.context.add(Message.builder().role(Message.Role.ASSISTANT).content("你好！请发一些消息吧").build());
        }
        this.context.add(Message.builder().role(Message.Role.USER).content(text).build());

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

        AsyncStatusInfo info = new AsyncStatusInfo()
                        .setMessageList(this.context)
                        .setNewText(text)
                        .setModel(model)
                        .setUi(UI.getCurrent())
                        .setText(text1)
                        .setUiContextKey(ContextConst.contextPrefix + getCharacterName())
                        .setSendButton(sendButton)
                        .setStayBottom(stayBottom)
                        .setVaadinSession(VaadinSession.getCurrent())
                        .setIp(RequestUtil.getRequestIp())
                ;
        try {
            chatResponseMonitor
                    .getChatResponseService(info)
                    .getChatResponseAsync(info);
        }catch (Exception e){
            log.error("请求回复运行中异常 " , e);
            sendButton.setEnabled(true);
            UI.getCurrent().push();
        }

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UI.getCurrent().getPushConfiguration().setPushMode(PushMode.MANUAL);
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
        context.clear();
        UI.getCurrent()
                .getSession()
                .setAttribute(ContextConst.contextPrefix + getCharacterName(),null);
        sendHello();
    }

    private void checkMessageListSize(){
        if (this.context.size() > 10){
            List<Message> tmpMessages = this.context
                    .subList(this.context.size() - 10
                            , this.context.size());
            this.context = new ArrayList<>(tmpMessages);
        }
    }

}
