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
@Table(name = "_quiz")
public class Quiz {

    @GeneratedValue
    @Id
    private Long id;
    private Long userId;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_id")
    private List<Question> questions;
    private String title;
    private boolean isDraft;
    private LocalDateTime createdAt;
    private LocalDateTime lastPlayed;
    @ManyToMany(mappedBy = "quizzes")
    private List<Folder> folders;
}
