package com.wxm158.quiz.quizplayservice.service;

import com.wxm158.quiz.quizplayservice.model.entity.Participant;
import com.wxm158.quiz.quizplayservice.model.entity.QuizSession;
import com.wxm158.quiz.quizplayservice.model.enums.Status;
import com.wxm158.quiz.quizplayservice.model.request.JoinRequest;
import com.wxm158.quiz.quizplayservice.repository.ParticipantRepository;
import com.wxm158.quiz.quizplayservice.repository.QuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository repository;
    private final QuizSessionRepository quizSessionRepository;

    public ResponseEntity<Participant> saveParticipant(JoinRequest joinRequest) {
        QuizSession quizSession = quizSessionRepository.findByJoinCode(joinRequest.getJoinCode());
        if (!repository.existsParticipantByAliasAndQuizSessionId(joinRequest.getAlias(), quizSession.getId())) {
            Participant newParticipant = Participant.builder()
                    .alias(joinRequest.getAlias())
                    .quizSession(quizSession)
                    .status(Status.ACTIVE)
                    .build();
            return ResponseEntity.ok(repository.save(newParticipant));
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    public List<Participant> findParticipants(Long quizSessionId) {
        List<Status> statuses = new ArrayList<>();
        statuses.add(Status.ACTIVE);
        statuses.add(Status.OFFLINE);
        return repository.findAllByStatusAndQuizSessionId(statuses, quizSessionId);
    }
}
