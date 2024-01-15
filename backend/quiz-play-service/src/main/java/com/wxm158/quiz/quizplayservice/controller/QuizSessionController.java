package com.wxm158.quiz.quizplayservice.controller;

import com.wxm158.quiz.quizplayservice.model.request.NextQuestion;
import com.wxm158.quiz.quizplayservice.model.request.ParticipantMessage;
import com.wxm158.quiz.quizplayservice.model.entity.Question;
import com.wxm158.quiz.quizplayservice.model.enums.State;
import com.wxm158.quiz.quizplayservice.service.QuizSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QuizSessionController {

    private final QuizSessionService quizSessionService;

//    Method for sending answer
    @MessageMapping("/quiz.sendAnswer")
    @SendTo("/topic/public")
    public ParticipantMessage sendAnswer(@Payload ParticipantMessage answer) {
//        Implement saving answer if type is answer

        return quizSessionService.sendAnswer(answer);
    }

    @MessageMapping("/quiz.addParticipant")
    @SendTo("/topic/public")
    public ParticipantMessage addParticipant(@Payload ParticipantMessage user, SimpMessageHeaderAccessor headerAccessor) {
//        Add participant in websocket session
        if(quizSessionService.saveParticipant(user) != null) {
            headerAccessor.getSessionAttributes().put("username", user.getParticipant());
            headerAccessor.getSessionAttributes().put("sessionId", user.getQuizSessionId());
            return user;
        } else {
            return null;
        }

    }

    // Send State?? Return current question
    @MessageMapping("/quiz.nextQuestion")
    @SendTo("/topic/question")
    public Question nextQuestion(@Payload NextQuestion nextQuestion) {
        if (nextQuestion.getState() == State.QUESTION) {
            return quizSessionService.nextQuestion(nextQuestion);
        } else {
            return null;
        }
    }

    @MessageMapping("/quiz.updateState")
    @SendTo("/topic/state")
    public State updateState(@Payload NextQuestion nextQuestion) {
        return quizSessionService.updateState(nextQuestion);
    }

}
