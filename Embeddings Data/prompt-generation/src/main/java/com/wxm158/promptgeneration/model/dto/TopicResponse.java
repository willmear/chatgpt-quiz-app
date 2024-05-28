package com.wxm158.promptgeneration.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TopicResponse {
    private List<String> topic;
}
