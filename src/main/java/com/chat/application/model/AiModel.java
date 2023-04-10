package com.chat.application.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AiModel {

    private String provider;

    private String modelName;

}
