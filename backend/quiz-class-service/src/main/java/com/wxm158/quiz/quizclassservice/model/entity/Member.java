package com.wxm158.quiz.quizclassservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_member")
@Entity
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private Long memberId;
    private Long classroomId;
}
