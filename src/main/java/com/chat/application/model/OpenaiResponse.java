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

    private Usage usage;
    @Data
    public static class Choice {
        private Message message;
        private Integer index;
        private String finish_reason;
    }

    @Data
    public class Usage {
        private Integer prompt_tokens;

        private Integer completion_tokens;

        private Integer total_tokens;
    }
}
