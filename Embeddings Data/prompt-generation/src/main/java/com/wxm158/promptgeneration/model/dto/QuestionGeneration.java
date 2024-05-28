package com.wxm158.promptgeneration.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionGeneration {
    private Long id;
    private String question;
    private List<String> choices;
    private int answer;
    private List<String> topics;
}
