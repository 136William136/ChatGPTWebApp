package com.chat.application.util;

import com.chat.application.constant.ImageConst;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.server.StreamResource;

public class ImageUtil {
    public static Avatar getAvatar(ImageConst imageConst){
        Avatar avatar = new Avatar(imageConst.getName());
        avatar.setImageResource(new StreamResource(imageConst.getFileName(), () ->
                ImageUtil.class.getResourceAsStream(imageConst.getPath())
        ));
        return avatar;
    }

}
