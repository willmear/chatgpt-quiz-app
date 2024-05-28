package com.wxm158.quiz.quizclassservice.repository;

import com.wxm158.quiz.quizclassservice.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
