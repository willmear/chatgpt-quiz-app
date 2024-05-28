package com.wxm158.quiz.quizcoreservice.service;

import com.wxm158.quiz.quizcoreservice.mapper.QuestionMapper;
import com.wxm158.quiz.quizcoreservice.model.dto.request.QuestionRequest;
import com.wxm158.quiz.quizcoreservice.model.dto.request.QuizSessionRequest;
import com.wxm158.quiz.quizcoreservice.model.entity.Folder;
import com.wxm158.quiz.quizcoreservice.model.entity.Question;
import com.wxm158.quiz.quizcoreservice.model.entity.Quiz;
import com.wxm158.quiz.quizcoreservice.repository.FolderRepository;
import com.wxm158.quiz.quizcoreservice.repository.QuestionRepository;
import com.wxm158.quiz.quizcoreservice.repository.QuizRepository;
import com.wxm158.quiz.quizcoreservice.service.rest.PlayServiceRestClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final FolderRepository folderRepository;
    private final QuestionMapper mapper;
    private final PlayServiceRestClient playServiceRestClient;

    public Quiz createQuiz(List<QuestionRequest> questionList, String userId) {
        if (userId == null || questionList.isEmpty()) { // Check if the userId is null or if the question list is empty
            return null;
        }

        List<Question> questions = questionRepository.saveAll(mapper.mapQuestionGenerationsToQuestions(questionList));

        Quiz quiz = Quiz.builder()
                .userId(Long.valueOf(userId))
                .questions(questions)
                .title("Untitled Quiz")
                .createdAt(LocalDateTime.now())
                .isDraft(true)
                .build();

        return quizRepository.save(quiz);
    }

    public Quiz getQuiz(String quizId, String userId) {
        try {
            Long parsedQuizId = Long.parseLong(quizId);
            Long parsedUserId = Long.parseLong(userId);
            return quizRepository.findByIdAndUserId(parsedQuizId, parsedUserId).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ResponseEntity<Quiz> publishQuiz(String quizId, String userId, Quiz newQuiz) {

        return quizRepository.findByIdAndUserId(Long.valueOf(quizId), Long.valueOf(userId))
                .map(quiz -> {
                    quiz.setQuestions(newQuiz.getQuestions());
                    quiz.setTitle(newQuiz.getTitle());
                    quiz.setDraft(false);
                    quiz.setCreatedAt(LocalDateTime.now());
                    Quiz updatedQuiz = quizRepository.save(quiz);
                    return ResponseEntity.ok(updatedQuiz);
                })
                .orElse(ResponseEntity.notFound().build());

    }

    public List<Quiz> getAllQuiz(String userId) {
        return quizRepository.findAllByUserIdAndIsDraft(Long.valueOf(userId), false);
    }

    public List<Quiz> getAllDraft(String userId) {
        return quizRepository.findAllByUserIdAndIsDraft(Long.valueOf(userId), true);
    }

    public Folder createFolder(String userId, Folder folder) {
        return folderRepository.save(Folder.builder()
                        .userId(Long.valueOf(userId))
                        .quizzes(folder.getQuizzes())
                        .description(folder.getDescription())
                        .build());
    }

    public ResponseEntity<Quiz> createQuizSession(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }
        QuizSessionRequest quizSessionRequest = QuizSessionRequest.builder()
                .quizCoreId(quiz.getId())
                .userId(quiz.getUserId())
                .questions(quiz.getQuestions())
                .title(quiz.getTitle())
                .build();
        log.info("creating session {}", quizId);
        return playServiceRestClient.createQuizSession(quizSessionRequest);
    }

    @Transactional
    public ResponseEntity<Void> deleteQuiz(Long quizId) {
        try {
            quizRepository.deleteById(quizId);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException ex) {
            // Quiz with the given ID does not exist
            return ResponseEntity.notFound().build();
        }
    }

    public Quiz getQuizStudent(Long quizId) {
        return quizRepository.findById(quizId).orElse(null);
    }

    public ResponseEntity<Quiz> updateLastPlayed(Long userId, Long quizId) {
        Optional<Quiz> optionalQuiz = quizRepository.findByIdAndUserId(quizId, userId);
        if (optionalQuiz.isPresent()) {
            Quiz quiz = optionalQuiz.get();
            quiz.setLastPlayed(LocalDateTime.now());
            quizRepository.save(quiz);
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
