package com.chat.application.model;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;

@Data
public class Choice {
    private Message delta;
    private Integer index;
    private String finish_reason;

}
