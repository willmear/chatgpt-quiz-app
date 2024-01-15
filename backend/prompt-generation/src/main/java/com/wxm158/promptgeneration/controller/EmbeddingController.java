package com.wxm158.promptgeneration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wxm158.promptgeneration.model.dto.ChatRequest;
import com.wxm158.promptgeneration.model.dto.QuestionGeneration;
import com.wxm158.promptgeneration.model.dto.TopicResponse;
import com.wxm158.promptgeneration.model.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.wxm158.promptgeneration.service.EmbeddingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/embedding")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    @PostMapping("/create")
    public ResponseEntity<List<QuestionGeneration>> createQuestion(@RequestBody ChatRequest chatRequest) {
        return ResponseEntity.ok(embeddingService.createQuestions(chatRequest));
    }

    @PostMapping("/save")
    public ResponseEntity<List<Question>> saveQuestions(
            @RequestBody List<QuestionGeneration> questions,
            @RequestHeader("x-auth-user-id") String userId
    ) {
        return ResponseEntity.ok(embeddingService.saveQuestions(questions, userId));
    }

    @GetMapping("/questions")
    public List<Question> getAllQuestions(@RequestHeader("x-auth-user-id") String userId) {
        return embeddingService.getAllQuestions(userId);
    }

}
