package com.wxm158.quiz.quizclassservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_classroom")
@Entity
public class Classroom {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String topic;
    private LocalDate createdAt = LocalDate.now();
//    @ManyToMany
//    @JoinTable(
//            name = "classroom_member",
//            joinColumns = @JoinColumn(name = "classroom_id"),
//            inverseJoinColumns = @JoinColumn(name = "member_id")
//    )
//    private List<Member> member;
}
