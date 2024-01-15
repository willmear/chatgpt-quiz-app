package com.wxm158.quiz.quizplayservice.model.entity;

import com.wxm158.quiz.quizplayservice.model.enums.State;
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
@Table(name = "_quiz_session")
public class QuizSession {

    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Long quizCoreId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    @Enumerated(EnumType.STRING)
    private State state;
    private String joinCode;
}
