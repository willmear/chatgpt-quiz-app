package com.wxm158.quiz.quizplayservice.repository;

import com.wxm158.quiz.quizplayservice.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
