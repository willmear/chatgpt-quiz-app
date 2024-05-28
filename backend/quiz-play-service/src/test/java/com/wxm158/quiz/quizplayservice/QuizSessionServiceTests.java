package com.wxm158.quiz.quizplayservice;

import com.wxm158.quiz.quizplayservice.model.dto.Answer;
import com.wxm158.quiz.quizplayservice.model.entity.Participant;
import com.wxm158.quiz.quizplayservice.model.entity.Question;
import com.wxm158.quiz.quizplayservice.model.entity.Quiz;
import com.wxm158.quiz.quizplayservice.model.entity.QuizSession;
import com.wxm158.quiz.quizplayservice.model.enums.MessageType;
import com.wxm158.quiz.quizplayservice.model.enums.State;
import com.wxm158.quiz.quizplayservice.model.enums.Status;
import com.wxm158.quiz.quizplayservice.model.request.NextQuestion;
import com.wxm158.quiz.quizplayservice.model.request.ParticipantMessage;
import com.wxm158.quiz.quizplayservice.repository.ParticipantRepository;
import com.wxm158.quiz.quizplayservice.repository.QuestionRepository;
import com.wxm158.quiz.quizplayservice.repository.QuizRepository;
import com.wxm158.quiz.quizplayservice.repository.QuizSessionRepository;
import com.wxm158.quiz.quizplayservice.service.QuizSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizSessionServiceTests {

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuizSessionService quizSessionService;

    @Test
    public void testCreateQuizSession() {
        // Create a sample quiz
        Quiz quiz = new Quiz();
        quiz.setUserId(1L);
        quiz.setQuizCoreId(123L);
        quiz.setQuestions(Collections.singletonList(new Question()));

        when(questionRepository.saveAll(quiz.getQuestions())).thenReturn(quiz.getQuestions());
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(quizSessionRepository.save(Mockito.any(QuizSession.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved quiz session as-is

        ResponseEntity<QuizSession> response = quizSessionService.createQuizSession(quiz);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(quiz, response.getBody().getQuiz());
    }

    @Test
    public void testGetQuizSession() {
        Long quizSessionId = 1L;
        String userId = "123";

        QuizSession expectedQuizSession = new QuizSession();
        when(quizSessionRepository.findByIdAndUserId(quizSessionId, Long.valueOf(userId))).thenReturn(expectedQuizSession);

        QuizSession result = quizSessionService.getQuizSession(quizSessionId, userId);

        assertEquals(expectedQuizSession, result);
    }


    @Test
    public void testVerifyJoinCode_ValidJoinCode() {
        String joinCode = "ABC123";

        when(quizSessionRepository.existsQuizSessionByJoinCodeAndState(joinCode, State.WAITING_FOR_PLAYERS)).thenReturn(true);
        QuizSession expectedQuizSession = new QuizSession();
        when(quizSessionRepository.findByJoinCode(joinCode)).thenReturn(expectedQuizSession);

        ResponseEntity<QuizSession> response = quizSessionService.verifyJoinCode(joinCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedQuizSession, response.getBody());
    }

    @Test
    public void testVerifyJoinCode_InvalidJoinCode() {
        String joinCode = "XYZ789";

        when(quizSessionRepository.existsQuizSessionByJoinCodeAndState(joinCode, State.WAITING_FOR_PLAYERS)).thenReturn(false);

        ResponseEntity<QuizSession> response = quizSessionService.verifyJoinCode(joinCode);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateState_ExistingSession() {
        Long sessionId = 1L;
        QuizSession quizSession = new QuizSession();
        quizSession.setState(State.WAITING_FOR_PLAYERS);

        when(quizSessionRepository.findById(sessionId)).thenReturn(Optional.of(quizSession));
        when(quizSessionRepository.save(quizSession)).thenReturn(quizSession);

        ResponseEntity<QuizSession> response = quizSessionService.updateState(quizSession, sessionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getState() == State.WAITING_FOR_PLAYERS);
        verify(quizSessionRepository, times(1)).save(quizSession);
    }

    @Test
    public void testUpdateState_NonExistingSession() {
        Long sessionId = 2L;

        when(quizSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        ResponseEntity<QuizSession> response = quizSessionService.updateState(new QuizSession(), sessionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(quizSessionRepository, never()).save(any());
    }

    @Test
    public void testSaveParticipant_Successful() {
        ParticipantMessage participantMessage = new ParticipantMessage();
        participantMessage.setQuizSessionId(1L);
        participantMessage.setParticipant("participant1");
        participantMessage.setType(MessageType.JOIN);

        QuizSession quizSession = new QuizSession();
        quizSession.setId(1L);

        when(quizSessionRepository.findById(participantMessage.getQuizSessionId())).thenReturn(Optional.of(quizSession));
        when(participantRepository.existsParticipantByAliasAndQuizSessionId(participantMessage.getParticipant(), quizSession.getId())).thenReturn(false);

        Participant expectedParticipant = Participant.builder()
                .alias("participant1")
                .quizSession(quizSession)
                .status(Status.ACTIVE)
                .build();

        when(participantRepository.save(any(Participant.class))).thenAnswer(invocation -> {
            Participant participant = invocation.getArgument(0);
            participant.setId(1L);
            return participant;
        });

        Participant savedParticipant = quizSessionService.saveParticipant(participantMessage);

        assertNotNull(savedParticipant);
        assertEquals(expectedParticipant.getAlias(), savedParticipant.getAlias());
        assertEquals(expectedParticipant.getQuizSession(), savedParticipant.getQuizSession());
        assertEquals(expectedParticipant.getStatus(), savedParticipant.getStatus());
        verify(participantRepository, times(1)).save(any(Participant.class));
    }

    @Test
    public void testSaveParticipant_AlreadyExists() {
        ParticipantMessage participantMessage = new ParticipantMessage();
        participantMessage.setQuizSessionId(1L);
        participantMessage.setParticipant("participant1");
        participantMessage.setType(MessageType.JOIN);

        QuizSession quizSession = new QuizSession();
        quizSession.setId(1L);

        when(quizSessionRepository.findById(participantMessage.getQuizSessionId())).thenReturn(Optional.of(quizSession));
        when(participantRepository.existsParticipantByAliasAndQuizSessionId(participantMessage.getParticipant(), quizSession.getId())).thenReturn(true);

        Participant savedParticipant = quizSessionService.saveParticipant(participantMessage);

        assertNull(savedParticipant);
        verify(participantRepository, never()).save(any());
    }

    @Test
    public void testSaveParticipant_InvalidSessionId() {
        ParticipantMessage participantMessage = new ParticipantMessage();
        participantMessage.setQuizSessionId(1L);
        participantMessage.setParticipant("participant1");
        participantMessage.setType(MessageType.JOIN);

        when(quizSessionRepository.findById(participantMessage.getQuizSessionId())).thenReturn(Optional.empty());

        Participant savedParticipant = quizSessionService.saveParticipant(participantMessage);

        assertNull(savedParticipant);
        verify(participantRepository, never()).save(any());
    }



    @Test
    public void testNextQuestion_StateIsQuestion() {
        NextQuestion nextQuestion = new NextQuestion();
        nextQuestion.setState(State.QUESTION);
        nextQuestion.setSessionId(1L);

        QuizSession quizSession = new QuizSession();
        quizSession.setId(1L);
        Quiz quiz = new Quiz();
        Question question = new Question();
        question.setId(1L);
        quiz.setQuestions(List.of(question));
        quizSession.setQuiz(quiz);

        when(quizSessionRepository.findById(nextQuestion.getSessionId())).thenReturn(Optional.of(quizSession));

        Question next = quizSessionService.nextQuestion(nextQuestion);

        assertEquals(question, next);
        verify(quizSessionRepository, times(1)).findById(nextQuestion.getSessionId());
    }

    @Test
    public void testNextQuestion_StateIsNotQuestion() {
        NextQuestion nextQuestion = new NextQuestion();
        nextQuestion.setState(State.LEADERBOARD);
        nextQuestion.setSessionId(1L);

        Question next = quizSessionService.nextQuestion(nextQuestion);

        assertEquals(null, next);
        verify(quizSessionRepository, never()).findById(nextQuestion.getSessionId());
    }

    @Test
    public void testUpdateState_QuizSessionExists() {
        NextQuestion nextQuestion = new NextQuestion();
        nextQuestion.setState(State.WAITING_FOR_PLAYERS);
        nextQuestion.setSessionId(1L);

        QuizSession quizSession = new QuizSession();
        quizSession.setId(1L);

        when(quizSessionRepository.findById(nextQuestion.getSessionId())).thenReturn(Optional.of(quizSession));
        when(quizSessionRepository.save(quizSession)).thenReturn(quizSession);

        State updatedState = quizSessionService.updateState(nextQuestion);

        assertEquals(nextQuestion.getState(), updatedState);
        assertEquals(nextQuestion.getState(), quizSession.getState());
        verify(quizSessionRepository, times(1)).findById(nextQuestion.getSessionId());
        verify(quizSessionRepository, times(1)).save(quizSession);
    }

    @Test
    public void testUpdateState_QuizSessionDoesNotExist() {
        NextQuestion nextQuestion = new NextQuestion();
        nextQuestion.setState(State.WAITING_FOR_PLAYERS);
        nextQuestion.setSessionId(1L);

        when(quizSessionRepository.findById(nextQuestion.getSessionId())).thenReturn(Optional.empty());

        State updatedState = quizSessionService.updateState(nextQuestion);

        assertEquals(nextQuestion.getState(), updatedState);
        verify(quizSessionRepository, times(1)).findById(nextQuestion.getSessionId());
        verify(quizSessionRepository, never()).save(any());
    }

    @Test
    public void testPlays() {
        Long quizCoreId = 123L;
        int expectedPlays = 5;

        when(quizSessionRepository.countAllByQuizCoreId(quizCoreId)).thenReturn(expectedPlays);

        Integer plays = quizSessionService.plays(quizCoreId);

        assertEquals(expectedPlays, plays);
    }


}
