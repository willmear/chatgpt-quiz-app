package com.wxm158.quiz.quizcoreservice.controller;

import com.wxm158.quiz.quizcoreservice.model.dto.request.QuestionRequest;
import com.wxm158.quiz.quizcoreservice.model.entity.Folder;
import com.wxm158.quiz.quizcoreservice.model.entity.Quiz;
import com.wxm158.quiz.quizcoreservice.service.CoreService;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/v1/core")
@RestController
public class CoreController {

    private final CoreService coreService;

    @PostMapping("/create")
    public ResponseEntity<Quiz> createQuiz(@RequestBody List<QuestionRequest> questions,
                                           @RequestHeader("x-auth-user-id") String userId) {
        return ResponseEntity.ok(coreService.createQuiz(questions, userId));
    }

    @GetMapping("/quiz/{quizId}")
    public Quiz getQuiz(@RequestHeader("x-auth-user-id") String userId,
                        @PathVariable String quizId) {
        return coreService.getQuiz(quizId, userId);
    }

    @GetMapping("/student/quiz/{quizId}")
    public Quiz getQuizStudent(@PathVariable String quizId) {
        return coreService.getQuizStudent(Long.valueOf(quizId));
    }

    @PutMapping("/quiz/{quizId}/publish")
    public ResponseEntity<Quiz> publishQuiz(@RequestHeader("x-auth-user-id") String userId,
                            @PathVariable String quizId,
                            @RequestBody Quiz quiz) {
        return coreService.publishQuiz(quizId, userId, quiz);
    }

    @GetMapping("/quiz/library")
    public List<Quiz> getAllQuiz(@RequestHeader("x-auth-user-id") String userId) {
        return coreService.getAllQuiz(userId);
    }

    @GetMapping("/quiz/library/draft")
    public List<Quiz> getAllDraft(@RequestHeader("x-auth-user-id") String userId) {
        return coreService.getAllDraft(userId);
    }

    @PostMapping("/quiz/library/folder")
    public ResponseEntity<Folder> createFolder(@RequestHeader("x-auth-user-id") String userId,
                                               @RequestBody Folder folder) {
        return ResponseEntity.ok(coreService.createFolder(userId, folder));
    }

    @PostMapping("/create/{quizId}")
    public ResponseEntity<Quiz> createQuizSession(@PathVariable Long quizId) {
        return coreService.createQuizSession(quizId);
    }

    @DeleteMapping("/quiz/delete/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        return coreService.deleteQuiz(quizId);
    }

    @PutMapping("/last-played")
    public ResponseEntity<Quiz> updateLastPlayed(@RequestHeader("x-auth-user-id") String userId,
                                                 @RequestBody Long quizId) {
        return coreService.updateLastPlayed(Long.valueOf(userId), quizId);
    }
}
