package com.wxm158.quiz.quizclassservice.repository;

import com.wxm158.quiz.quizclassservice.model.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
