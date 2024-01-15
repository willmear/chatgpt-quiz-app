package com.wxm158.quiz.quizcoreservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class QuestionRequest {

    private Long id;
    private Long userId;
    private String question;
    private List<String> choices;
    private List<Integer> answer;
    private List<String> topics;
    private LocalDateTime generatedAt;
    private String questionType;
}
