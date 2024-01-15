package com.wxm158.quiz.quizplayservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinRequest {
    private String alias;
    private String joinCode;
}
