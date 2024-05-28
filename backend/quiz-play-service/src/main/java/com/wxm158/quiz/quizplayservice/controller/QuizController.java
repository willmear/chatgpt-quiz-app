package com.wxm158.quiz.quizplayservice.controller;

import com.wxm158.quiz.quizplayservice.model.entity.Participant;
import com.wxm158.quiz.quizplayservice.model.entity.Quiz;
import com.wxm158.quiz.quizplayservice.model.entity.QuizSession;
import com.wxm158.quiz.quizplayservice.model.request.JoinRequest;
import com.wxm158.quiz.quizplayservice.service.ParticipantService;
import com.wxm158.quiz.quizplayservice.service.QuizSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/quiz")
@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizSessionService quizSessionService;
    private final ParticipantService participantService;

    @PostMapping("/create")
    ResponseEntity<QuizSession> createQuizSession(@RequestBody Quiz quiz) {
        return quizSessionService.createQuizSession(quiz);
    }

    @GetMapping("/session/{quizSessionId}")
    QuizSession getQuizSession(@PathVariable Long quizSessionId, @RequestHeader("x-auth-user-id") String userId) {
        return quizSessionService.getQuizSession(quizSessionId, userId);
    }

    @PostMapping("/join/verify")
    ResponseEntity<QuizSession> verifyJoinCode(@RequestBody String joinCode) {
        return quizSessionService.verifyJoinCode(joinCode);
    }

    @PostMapping("/join")
    ResponseEntity<Participant> join(@RequestBody JoinRequest joinRequest) {
        return participantService.saveParticipant(joinRequest);
    }

    @GetMapping("/participants/{quizSessionId}")
    List<Participant> participants(@PathVariable Long quizSessionId) {
        return participantService.findParticipants(quizSessionId);
    }

    @PutMapping("/state/{sessionId}")
    ResponseEntity<QuizSession> updateState(@PathVariable Long sessionId, @RequestBody QuizSession quizSession) {
        return quizSessionService.updateState(quizSession, sessionId);
    }

    @GetMapping("/plays/{quizCoreId}")
    Integer plays(@PathVariable Long quizCoreId) {
        return quizSessionService.plays(quizCoreId);
    }

}
