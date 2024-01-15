package com.wxm158.quiz.quizplayservice.model.entity;


import com.wxm158.quiz.quizplayservice.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_participant")
public class Participant {
    @Id
    @GeneratedValue
    private Long id;
    private String alias;
    private int points;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> answers;
    @ManyToOne
    @JoinColumn(name="quiz_session_id")
    private QuizSession quizSession;
    private boolean questionAnswered=false;
}
