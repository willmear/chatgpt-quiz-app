package com.wxm158.quiz.quizcoreservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_question")
public class Question {

    @Id
    @GeneratedValue
    private Long id;
    private String question;
    @ElementCollection
    private List<String> choices;
    @ElementCollection
    private List<Integer> answer;
    private String questionType;
    private int timeSeconds;
    private int points;
}
