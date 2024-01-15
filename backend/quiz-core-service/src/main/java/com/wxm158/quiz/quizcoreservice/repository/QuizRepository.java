package com.wxm158.quiz.quizcoreservice.repository;

import com.wxm158.quiz.quizcoreservice.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByIdAndUserId(Long id, Long userId);

    List<Quiz> findAllByUserIdAndIsDraft(Long valueOf, boolean b);
}
