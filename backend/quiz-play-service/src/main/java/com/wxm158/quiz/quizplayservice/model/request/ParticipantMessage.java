package com.wxm158.quiz.quizplayservice.model.request;

import com.wxm158.quiz.quizplayservice.model.enums.MessageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantMessage {

    private int answer;
    private String participant;
    private Long quizSessionId;
    private MessageType type;
}
