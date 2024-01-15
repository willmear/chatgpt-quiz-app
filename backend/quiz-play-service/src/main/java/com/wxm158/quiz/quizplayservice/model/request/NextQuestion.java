package com.wxm158.quiz.quizplayservice.model.request;

import com.wxm158.quiz.quizplayservice.model.enums.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NextQuestion {
    private State state;
    private Long sessionId;
}
