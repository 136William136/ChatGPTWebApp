package com.chat.application.model;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicLong;

@Data
@Accessors(chain = true)
public class UserQuota {

    private String ip;

    private AtomicLong currentQuota;

    private Long maxQuota;

    private String type;


}
