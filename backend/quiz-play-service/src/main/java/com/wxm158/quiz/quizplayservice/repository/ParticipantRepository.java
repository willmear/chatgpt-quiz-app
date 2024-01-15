package com.wxm158.quiz.quizplayservice.repository;

import com.wxm158.quiz.quizplayservice.model.entity.Participant;
import com.wxm158.quiz.quizplayservice.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllByStatus(Status status);

    boolean existsParticipantByAliasAndQuizSessionId(String alias, Long quizSessionId);

    @Query("SELECT p FROM Participant p WHERE p.status IN (:statuses) AND p.quizSession.id = :quizSessionId")
    List<Participant> findAllByStatusAndQuizSessionId(@Param("statuses") List<Status> statuses,
                                                      @Param("quizSessionId") Long quizSessionId);

    Participant findByAlias(String participant);

    Participant findByAliasAndQuizSessionId(String participant, Long quizSessionId);
}
