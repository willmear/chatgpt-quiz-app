package com.wxm158.promptgeneration.model.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String topic;
    private String questionType;
    private String questionAmount;
}
