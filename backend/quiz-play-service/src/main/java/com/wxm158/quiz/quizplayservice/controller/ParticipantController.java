package com.wxm158.quiz.quizplayservice.controller;

import com.wxm158.quiz.quizplayservice.model.entity.Participant;
import com.wxm158.quiz.quizplayservice.model.entity.QuizSession;
import com.wxm158.quiz.quizplayservice.model.request.JoinRequest;
import com.wxm158.quiz.quizplayservice.service.ParticipantService;
import com.wxm158.quiz.quizplayservice.service.QuizSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
public class ParticipantController {

    private final QuizSessionService quizSessionService;
    private final ParticipantService participantService;

    @PostMapping("/verify")
    ResponseEntity<QuizSession> verifyJoinCode(@RequestBody String joinCode) {
        return quizSessionService.verifyJoinCode(joinCode);
    }

    @PostMapping("")
    ResponseEntity<Participant> join(@RequestBody JoinRequest joinRequest) {
        return participantService.saveParticipant(joinRequest);
    }
}
