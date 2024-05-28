package com.wxm158.promptgeneration.controller;

import com.wxm158.promptgeneration.model.dto.ChatRequest;
import com.wxm158.promptgeneration.model.dto.QuestionGeneration;
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

    @PostMapping("/embed")
    public ResponseEntity<List<String>> createEmbedding() {
        return embeddingService.createEmbedding();
    }

    @PostMapping("/generate")
    public ResponseEntity<List<QuestionGeneration>> generateQuestions(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(embeddingService.generateQuestions(request.getTopic()));
    }

}
