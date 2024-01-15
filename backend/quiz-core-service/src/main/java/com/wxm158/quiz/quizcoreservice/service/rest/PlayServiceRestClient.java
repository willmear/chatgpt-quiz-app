package com.wxm158.quiz.quizcoreservice.service.rest;

import com.wxm158.quiz.quizcoreservice.model.dto.request.QuizSessionRequest;
import com.wxm158.quiz.quizcoreservice.model.entity.Quiz;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "quiz-play-service")
public interface PlayServiceRestClient {

    @PostMapping("api/v1/quiz/create")
    ResponseEntity<Quiz> createQuizSession(@RequestBody QuizSessionRequest quiz);

}
