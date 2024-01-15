package com.wxm158.quiz.quizplayservice.repository;

import com.wxm158.quiz.quizplayservice.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

}
