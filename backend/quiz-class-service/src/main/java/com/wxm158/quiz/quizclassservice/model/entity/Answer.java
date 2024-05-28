package com.wxm158.quiz.quizclassservice.model.entity;

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
@Table(name = "_answer")
public class Answer {
    @Id
    @GeneratedValue
    private Long id;
    private String questionType;
    @ElementCollection
    private List<Integer> answer;
}
