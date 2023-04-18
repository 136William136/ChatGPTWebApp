package com.chat.application.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OpenaiResponse extends AiResponse{

    private String id;
    private String object;
    private Long created;

    private String model;

    private List<Choice> choices;
}
