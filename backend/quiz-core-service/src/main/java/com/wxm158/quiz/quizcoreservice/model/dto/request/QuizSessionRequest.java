package com.wxm158.quiz.quizcoreservice.model.dto.request;

import com.wxm158.quiz.quizcoreservice.model.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizSessionRequest {
    private Long quizCoreId;
    private Long userId;
    private List<Question> questions;
    private String title;
}
