package com.wxm158.quiz.quizplayservice.model.entity;

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
@Table(name = "_question")
public class Question {
    @Id
    @GeneratedValue
    private Long id;
    private String question;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> choices;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> answer;
    private String questionType;
    private int timeSeconds;
    private int points;
}
