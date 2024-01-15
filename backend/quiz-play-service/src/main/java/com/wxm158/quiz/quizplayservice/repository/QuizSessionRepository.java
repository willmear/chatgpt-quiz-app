package com.wxm158.quiz.quizplayservice.repository;

import com.wxm158.quiz.quizplayservice.model.entity.Quiz;
import com.wxm158.quiz.quizplayservice.model.entity.QuizSession;
import com.wxm158.quiz.quizplayservice.model.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
    Optional<QuizSession> findByUserId(Long userId);

    Quiz findQuizById(Long quizSessionId);

    QuizSession findByJoinCode(String joinCode);

    Optional<QuizSession> findByJoinCodeAndStateNot(String joinCode, State state);

    QuizSession findByQuizCoreIdAndUserId(Long quizCoreId, Long userId);

    boolean existsQuizSessionByJoinCodeAndState(String joinCode, State state);


    QuizSession findByIdAndUserId(Long id, Long userId);
}
