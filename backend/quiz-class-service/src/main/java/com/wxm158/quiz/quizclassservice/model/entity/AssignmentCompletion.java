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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_assignment_completion")
public class AssignmentCompletion {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    private Integer completionTime;
    private LocalDateTime submittedAt;
    private Boolean exitedBeforeFinished;
    @ElementCollection
    @CollectionTable(name = "assignment_completion_answers", joinColumns = @JoinColumn(name = "assignment_completion_id"))
    @Column(name = "answer", columnDefinition = "boolean")
    private List<Boolean> answer;
    @OneToMany
    @JoinColumn(name = "completion_id")
    private List<Answer> answers;
    private Boolean timeOver;
}
