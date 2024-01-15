package com.wxm158.quiz.quizcoreservice.repository;

import com.wxm158.quiz.quizcoreservice.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
