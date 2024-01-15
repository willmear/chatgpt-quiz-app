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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoreService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final FolderRepository folderRepository;
    private final QuestionMapper mapper;
    private final PlayServiceRestClient playServiceRestClient;

    public Quiz createQuiz(List<QuestionRequest> questionList, String userId) {


        List<Question> questions = questionRepository.saveAll(mapper.mapQuestionGenerationsToQuestions(questionList));

        Quiz quiz = Quiz.builder()
                .userId(Long.valueOf(userId))
                .questions(questions)
                .title("Untitled Quiz")
                .isDraft(true)
                .build();

        return quizRepository.save(quiz);
    }

    public Quiz getQuiz(String quizId, String userId) {

        return quizRepository.findByIdAndUserId(Long.valueOf(quizId), Long.valueOf(userId)).orElse(null);

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
        QuizSessionRequest quizSessionRequest = QuizSessionRequest.builder()
                .quizCoreId(quiz.getId())
                .userId(quiz.getUserId())
                .questions(quiz.getQuestions())
                .title(quiz.getTitle())
                .build();
        return playServiceRestClient.createQuizSession(quizSessionRequest);
    }

    @Transactional
    public ResponseEntity<Void> deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
        return ResponseEntity.noContent().build();
    }
}
