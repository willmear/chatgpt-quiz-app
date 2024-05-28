package com.wxm158.quiz.quizplayservice.service;

import com.wxm158.quiz.quizplayservice.model.entity.*;
import com.wxm158.quiz.quizplayservice.model.enums.MessageType;
import com.wxm158.quiz.quizplayservice.model.enums.State;
import com.wxm158.quiz.quizplayservice.model.enums.Status;
import com.wxm158.quiz.quizplayservice.model.request.NextQuestion;
import com.wxm158.quiz.quizplayservice.model.request.ParticipantMessage;
import com.wxm158.quiz.quizplayservice.repository.ParticipantRepository;
import com.wxm158.quiz.quizplayservice.repository.QuestionRepository;
import com.wxm158.quiz.quizplayservice.repository.QuizRepository;
import com.wxm158.quiz.quizplayservice.repository.QuizSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizSessionService {

    private final QuizSessionRepository repository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ParticipantRepository participantRepository;


    private String generateUUID() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0,8);

        if (repository.findByJoinCodeAndStateNot(uuid, State.FINISHED).isPresent()) {
            return generateUUID();
        } else {
            return uuid;
        }

    }

//    CREATE QUIZ
    public ResponseEntity<QuizSession> createQuizSession(Quiz quiz) {
        List<Question> questionList = questionRepository.saveAll(quiz.getQuestions());
        log.info("saved questions {}", questionList);
        quiz.setQuestions(questionList);
        Quiz savedQuiz = quizRepository.save(quiz);
        QuizSession quizSession = QuizSession.builder()
                .joinCode(generateUUID())
                .userId(quiz.getUserId())
                .state(State.WAITING_FOR_PLAYERS)
                .quiz(savedQuiz)
                .quizCoreId(quiz.getQuizCoreId())
                .build();
        return ResponseEntity.ok(repository.save(quizSession));
    }

    public QuizSession getQuizSession(Long quizSessionId, String userId) {
        return repository.findByIdAndUserId(quizSessionId, Long.valueOf(userId));
    }

    public ResponseEntity<QuizSession> verifyJoinCode(String joinCode) {
        System.out.println(joinCode);
        if (repository.existsQuizSessionByJoinCodeAndState(joinCode, State.WAITING_FOR_PLAYERS)) {
            return ResponseEntity.ok(repository.findByJoinCode(joinCode));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<QuizSession> updateState(QuizSession quizSession, Long sessionId) {
        return repository.findById(sessionId)
                .map(quiz -> {
                    quiz.setState(quizSession.getState());
                    quiz.setQuiz(quizSession.getQuiz());
                    QuizSession updatedQuizSession = repository.save(quiz);
                    return ResponseEntity.ok(updatedQuizSession);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /*
    * Web Socket Service
    * Save Participant
    * */
    public Participant saveParticipant(ParticipantMessage participantMessage) {
        QuizSession quizSession = repository.findById(participantMessage.getQuizSessionId()).orElse(null);
        if (quizSession == null) {
            return null;
        }
        if (!participantRepository.existsParticipantByAliasAndQuizSessionId(participantMessage.getParticipant(),
                quizSession.getId()) && participantMessage.getType() == MessageType.JOIN) {
            Participant newParticipant = Participant.builder()
                    .alias(participantMessage.getParticipant())
                    .quizSession(quizSession)
                    .status(Status.ACTIVE)
                    .build();
            return participantRepository.save(newParticipant);
        } else {
            return null;
        }

    }

    /*
     * Web Socket Service
     * Send Answer
     * */
    public ParticipantMessage sendAnswer(ParticipantMessage answer) {
        Participant participant = participantRepository.findByAliasAndQuizSessionId(answer.getParticipant(), answer.getQuizSessionId());
        QuizSession quizSession = repository.findById(answer.getQuizSessionId()).orElse(null);
        Quiz quiz = quizSession.getQuiz();
        Question question = quiz.getQuestions().get(quiz.getCurrentQuestion());

        List<Boolean> participantAnswers = participant.getAnswers();
        log.info("question type {}", question.getQuestionType());
        if (question.getQuestionType().equals("Ordering")) {
            var points = participant.getPoints();
            for (int i=0; i< question.getAnswer().size(); i++) {
                if (Objects.equals(question.getAnswer().get(i), Integer.valueOf(answer.getAnswer().getAnswer().get(i)))) {
                    points += question.getPoints()/question.getChoices().size();
                }
            }
            participant.setPoints(points);
            participantAnswers.add(Boolean.TRUE);
            participant.setAnswers(participantAnswers);
            participantRepository.save(participant);
            return answer;
        } else if (question.getQuestionType().equals("Drag and Drop")) {
            if (answer.getAnswer().getAnswer().equals(question.getAnswer())) {
                participant.setPoints(participant.getPoints()+question.getPoints());
                participantAnswers.add(Boolean.TRUE);
            } else {
                participantAnswers.add(Boolean.FALSE);
            }
            participant.setAnswers(participantAnswers);
            participantRepository.save(participant);
            return answer;
        } else if (question.getQuestionType().equals("Fill The Blanks")) {
            if (question.getAnswer().contains(answer.getAnswer().getAnswer().get(0))) {
                participant.setPoints(participant.getPoints()+question.getPoints());
                participantAnswers.add(Boolean.TRUE);
            } else {
                participantAnswers.add(Boolean.FALSE);
            }
            participant.setAnswers(participantAnswers);
            participantRepository.save(participant);
            return answer;
        }
        else {
            for (Integer i: question.getAnswer()) {
                if (Objects.equals(i, Integer.valueOf(answer.getAnswer().getAnswer().get(0)))) {
                    var points = participant.getPoints();
                    points += question.getPoints();
                    participant.setPoints(points);
                    participantAnswers.add(Boolean.TRUE);
                    participant.setAnswers(participantAnswers);
                    participantRepository.save(participant);
                    return answer;
                }
            }
        }


        return answer;

    }

    public Question nextQuestion(NextQuestion nextQuestion) {
        if (nextQuestion.getState()==State.QUESTION) {
            QuizSession quizSession = repository.findById(nextQuestion.getSessionId()).orElse(null);
            assert quizSession != null;
            Quiz quiz = quizSession.getQuiz();
            List<Question> questions = quiz.getQuestions();
            return questions.get(quiz.getCurrentQuestion());
        }

        return null;

    }

    public State updateState(NextQuestion nextQuestion) {
        QuizSession quizSession = repository.findById(nextQuestion.getSessionId()).orElse(null);
        if (quizSession != null) {
            quizSession.setState(nextQuestion.getState());
            repository.save(quizSession);
        }
        return nextQuestion.getState();
    }

    public Integer plays(Long quizCoreId) {
        return repository.countAllByQuizCoreId(quizCoreId);
    }
}
