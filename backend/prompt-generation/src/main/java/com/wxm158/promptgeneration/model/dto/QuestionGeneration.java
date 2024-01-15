package com.wxm158.promptgeneration.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionGeneration {
    private Long id;
    private String question;
    private List<String> choices;
    private List<Integer> answer;
    private List<String> topics;
    private String questionType;
}
