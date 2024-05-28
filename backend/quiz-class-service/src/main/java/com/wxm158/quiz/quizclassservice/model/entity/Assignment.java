package com.wxm158.quiz.quizclassservice.model.entity;

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
@Table(name = "_assignment")
public class Assignment {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;
    private LocalDateTime releaseTime;
    private LocalDateTime deadline;
//    @OneToMany
//    @JoinColumn(name = "question_id")
//    private List<Question> questions;
    @ManyToMany
    @JoinTable(
            name = "assignment_question",
            joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;
    private Double timer;
    private Boolean multipleAttempts;
}
