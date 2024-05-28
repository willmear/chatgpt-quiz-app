package com.wxm158.quiz.quizcoreservice;

import com.wxm158.quiz.quizcoreservice.mapper.QuestionMapper;
import com.wxm158.quiz.quizcoreservice.model.dto.request.QuestionRequest;
import com.wxm158.quiz.quizcoreservice.model.dto.request.QuizSessionRequest;
import com.wxm158.quiz.quizcoreservice.model.entity.Folder;
import com.wxm158.quiz.quizcoreservice.model.entity.Question;
import com.wxm158.quiz.quizcoreservice.model.entity.Quiz;
import com.wxm158.quiz.quizcoreservice.repository.FolderRepository;
import com.wxm158.quiz.quizcoreservice.repository.QuestionRepository;
import com.wxm158.quiz.quizcoreservice.repository.QuizRepository;
import com.wxm158.quiz.quizcoreservice.service.CoreService;
import com.wxm158.quiz.quizcoreservice.service.rest.PlayServiceRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizCoreServiceApplicationTests {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private QuizRepository quizRepository;

	@Mock
	private FolderRepository folderRepository;

	@Mock
	private QuestionMapper mapper;


	@Mock
	private PlayServiceRestClient playServiceRestClient;


	@InjectMocks
	private CoreService coreService;

	@Test
	public void testCreateQuiz_Successful() {
		List<QuestionRequest> questionList = new ArrayList<>();
		QuestionRequest questionRequest = QuestionRequest.builder()
				.id(1L)
				.userId(1L)
				.question("Sample question")
				.choices(List.of("Choice 1", "Choice 2", "Choice 3"))
				.answer(List.of(1))
				.topics(List.of("Topic 1", "Topic 2"))
				.generatedAt(LocalDateTime.now())
				.questionType("Type")
				.build();
		questionList.add(questionRequest);
		String userId = "1";

		when(questionRepository.saveAll(any())).thenReturn(new ArrayList<>());

		Quiz savedQuiz = Quiz.builder()
				.id(1L)
				.userId(Long.valueOf(userId))
				.questions(new ArrayList<>())
				.title("Untitled Quiz")
				.createdAt(LocalDateTime.now())
				.isDraft(true)
				.build();
		when(quizRepository.save(any())).thenReturn(savedQuiz);

		Quiz createdQuiz = coreService.createQuiz(questionList, userId);

		assertNotNull(createdQuiz);
		assertEquals(userId, String.valueOf(createdQuiz.getUserId()));
		assertEquals(0, createdQuiz.getQuestions().size());
		assertEquals("Untitled Quiz", createdQuiz.getTitle());
		assertTrue(createdQuiz.isDraft());
		assertNotNull(createdQuiz.getCreatedAt());
		verify(questionRepository, times(1)).saveAll(any());
		verify(quizRepository, times(1)).save(any());
	}

	@Test
	public void testCreateQuiz_EmptyQuestionList() {
		List<QuestionRequest> questionList = new ArrayList<>();
		String userId = "1";

		Quiz createdQuiz = coreService.createQuiz(questionList, userId);

		assertNull(createdQuiz);
		verify(questionRepository, never()).saveAll(any());
		verify(quizRepository, never()).save(any());
	}

	@Test
	public void testCreateQuiz_NullUserId() {
		List<QuestionRequest> questionList = new ArrayList<>();
		QuestionRequest questionRequest = QuestionRequest.builder()
				.id(1L)
				.userId(1L)
				.question("Sample question")
				.choices(List.of("Choice 1", "Choice 2", "Choice 3"))
				.answer(List.of(1))
				.topics(List.of("Topic 1", "Topic 2"))
				.generatedAt(LocalDateTime.now())
				.questionType("Type")
				.build();
		questionList.add(questionRequest);
		String userId = null;

		Quiz createdQuiz = coreService.createQuiz(questionList, userId);

		assertNull(createdQuiz);
		verify(questionRepository, never()).saveAll(any());
		verify(quizRepository, never()).save(any());
	}


	@Test
	public void testGetQuiz_Successful() {
		String quizId = "1";
		String userId = "1";
		Quiz expectedQuiz = Quiz.builder()
				.id(1L)
				.userId(1L)
				.title("Sample Quiz")
				.build();

		when(quizRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(expectedQuiz));

		Quiz retrievedQuiz = coreService.getQuiz(quizId, userId);

		assertNotNull(retrievedQuiz);
		assertEquals(expectedQuiz, retrievedQuiz);
		verify(quizRepository, times(1)).findByIdAndUserId(1L, 1L);
	}

	@Test
	public void testGetQuiz_InvalidQuizId() {
		String quizId = "invalidId";
		String userId = "1";

		Quiz retrievedQuiz = coreService.getQuiz(quizId, userId);

		assertNull(retrievedQuiz);

		Long parsedQuizId = null;
		Long parsedUserId = null;
		try {
			parsedQuizId = Long.parseLong(quizId);
			parsedUserId = Long.parseLong(userId);
		} catch (NumberFormatException e) {
		}

		if (parsedQuizId != null && parsedUserId != null) {
			verify(quizRepository, times(1)).findByIdAndUserId(eq(parsedQuizId), eq(parsedUserId));
		} else {
			verify(quizRepository, never()).findByIdAndUserId(any(), any());
		}
	}


	@Test
	public void testGetQuiz_InvalidUserId() {
		String quizId = "1";
		String userId = "invalidId";

		Quiz retrievedQuiz = coreService.getQuiz(quizId, userId);

		assertNull(retrievedQuiz);

		verify(quizRepository, never()).findByIdAndUserId(any(), any());
	}

	@Test
	public void testPublishQuiz_Successful() {
		String quizId = "1";
		String userId = "1";
		Quiz existingQuiz = Quiz.builder()
				.id(1L)
				.userId(1L)
				.title("Existing Quiz")
				.isDraft(true)
				.createdAt(LocalDateTime.now())
				.build();
		Quiz newQuiz = Quiz.builder()
				.title("Published Quiz")
				.isDraft(false)
				.createdAt(LocalDateTime.now())
				.build();

		when(quizRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingQuiz));
		when(quizRepository.save(existingQuiz)).thenReturn(existingQuiz);

		ResponseEntity<Quiz> response = coreService.publishQuiz(quizId, userId, newQuiz);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(existingQuiz, response.getBody());
		assertFalse(existingQuiz.isDraft());
		assertEquals("Published Quiz", existingQuiz.getTitle());
		verify(quizRepository, times(1)).findByIdAndUserId(1L, 1L);
		verify(quizRepository, times(1)).save(existingQuiz);
	}

	@Test
	public void testPublishQuiz_QuizNotFound() {
		String quizId = "1";
		String userId = "1";
		Quiz newQuiz = Quiz.builder()
				.title("Published Quiz")
				.isDraft(false)
				.createdAt(LocalDateTime.now())
				.build();

		when(quizRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

		ResponseEntity<Quiz> response = coreService.publishQuiz(quizId, userId, newQuiz);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(quizRepository, times(1)).findByIdAndUserId(1L, 1L);
		verify(quizRepository, never()).save(any());
	}

	@Test
	public void testGetAllQuiz_Successful() {
		String userId = "1";
		Quiz quiz1 = Quiz.builder()
				.id(1L)
				.userId(1L)
				.title("Quiz 1")
				.isDraft(false)
				.createdAt(LocalDateTime.now())
				.build();
		Quiz quiz2 = Quiz.builder()
				.id(2L)
				.userId(1L)
				.title("Quiz 2")
				.isDraft(false)
				.createdAt(LocalDateTime.now())
				.build();
		List<Quiz> expectedQuizzes = Arrays.asList(quiz1, quiz2);

		when(quizRepository.findAllByUserIdAndIsDraft(1L, false)).thenReturn(expectedQuizzes);

		List<Quiz> result = coreService.getAllQuiz(userId);

		assertEquals(expectedQuizzes.size(), result.size());
		assertEquals(expectedQuizzes, result);
	}

	@Test
	public void testGetAllDraft_Successful() {
		String userId = "1";
		Quiz draftQuiz1 = Quiz.builder()
				.id(1L)
				.userId(1L)
				.title("Draft Quiz 1")
				.isDraft(true)
				.createdAt(LocalDateTime.now())
				.build();
		Quiz draftQuiz2 = Quiz.builder()
				.id(2L)
				.userId(1L)
				.title("Draft Quiz 2")
				.isDraft(true)
				.createdAt(LocalDateTime.now())
				.build();
		List<Quiz> expectedDraftQuizzes = Arrays.asList(draftQuiz1, draftQuiz2);

		when(quizRepository.findAllByUserIdAndIsDraft(1L, true)).thenReturn(expectedDraftQuizzes);

		List<Quiz> result = coreService.getAllDraft(userId);

		assertEquals(expectedDraftQuizzes.size(), result.size());
		assertEquals(expectedDraftQuizzes, result);
	}

	@Test
	public void testCreateFolder_Successful() {
		String userId = "1";
		Folder folderToCreate = Folder.builder()
				.userId(1L)
				.quizzes(Arrays.asList(new Quiz(), new Quiz()))
				.description("Folder Description")
				.build();
		Folder expectedFolder = Folder.builder()
				.userId(1L)
				.quizzes(folderToCreate.getQuizzes())
				.description(folderToCreate.getDescription())
				.build();

		when(folderRepository.save(folderToCreate)).thenReturn(expectedFolder);

		Folder result = coreService.createFolder(userId, folderToCreate);

		assertEquals(expectedFolder, result);
	}

	@Test
	public void testCreateQuizSession_Successful() {
		long quizId = 1L;
		Quiz quiz = new Quiz();
		quiz.setId(quizId);
		quiz.setUserId(123L);
		quiz.setTitle("Test Quiz");
		quiz.setQuestions(Arrays.asList(new Question(), new Question()));

		QuizSessionRequest expectedRequest = QuizSessionRequest.builder()
				.quizCoreId(quizId)
				.userId(quiz.getUserId())
				.questions(quiz.getQuestions())
				.title(quiz.getTitle())
				.build();

		ResponseEntity<Quiz> mockResponse = new ResponseEntity<>(quiz, HttpStatus.CREATED);

		when(quizRepository.findById(quizId)).thenReturn(java.util.Optional.of(quiz));

		when(playServiceRestClient.createQuizSession(expectedRequest)).thenReturn(mockResponse);

		ResponseEntity<Quiz> result = coreService.createQuizSession(quizId);

		assertEquals(mockResponse, result);
		verify(quizRepository, times(1)).findById(quizId);
		verify(playServiceRestClient, times(1)).createQuizSession(expectedRequest);
	}

	@Test
	public void testCreateQuizSession_QuizNotFound() {
		long quizId = 1L;

		when(quizRepository.findById(quizId)).thenReturn(java.util.Optional.empty());

		ResponseEntity<Quiz> result = coreService.createQuizSession(quizId);

		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
		verify(quizRepository, times(1)).findById(quizId);
		verify(playServiceRestClient, never()).createQuizSession(any());
	}

	@Test
	@Transactional
	public void testDeleteQuiz_Successful() {
		long quizId = 1L;

		doNothing().when(quizRepository).deleteById(quizId);

		ResponseEntity<Void> result = coreService.deleteQuiz(quizId);

		assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
		verify(quizRepository, times(1)).deleteById(quizId);
	}

	@Test
	public void testDeleteQuiz_QuizNotFound() {
		long quizId = 1L;

		doThrow(EmptyResultDataAccessException.class).when(quizRepository).deleteById(quizId);

		ResponseEntity<Void> result = coreService.deleteQuiz(quizId);

		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
		verify(quizRepository, times(1)).deleteById(quizId);
	}

	@Test
	public void testGetQuizStudent_ExistingQuiz() {
		long quizId = 1L;
		Quiz expectedQuiz = new Quiz();
		expectedQuiz.setId(quizId);

		when(quizRepository.findById(quizId)).thenReturn(Optional.of(expectedQuiz));

		Quiz result = coreService.getQuizStudent(quizId);

		assertNotNull(result);
		assertEquals(expectedQuiz, result);
		assertEquals(quizId, result.getId());
		verify(quizRepository, times(1)).findById(quizId);
	}

	@Test
	public void testGetQuizStudent_NonExistingQuiz() {
		long quizId = 1L;

		when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

		Quiz result = coreService.getQuizStudent(quizId);

		assertNull(result);
		verify(quizRepository, times(1)).findById(quizId);
	}

	@Test
	public void testUpdateLastPlayed_ExistingQuiz() {
		long userId = 1L;
		long quizId = 1L;
		LocalDateTime currentTime = LocalDateTime.now();
		Quiz quiz = new Quiz();
		quiz.setId(quizId);
		quiz.setUserId(userId);

		when(quizRepository.findByIdAndUserId(quizId, userId)).thenReturn(Optional.of(quiz));
		when(quizRepository.save(any())).thenReturn(quiz);

		ResponseEntity<Quiz> response = coreService.updateLastPlayed(userId, quizId);

		assertNotNull(response);
		assertEquals(ResponseEntity.ok(quiz), response);
		assertEquals(currentTime.withNano(0), quiz.getLastPlayed().withNano(0)); // Ensure lastPlayed is updated
		verify(quizRepository, times(1)).findByIdAndUserId(quizId, userId);
		verify(quizRepository, times(1)).save(quiz);
	}

	@Test
	public void testUpdateLastPlayed_NonExistingQuiz() {
		long userId = 1L;
		long quizId = 1L;

		when(quizRepository.findByIdAndUserId(quizId, userId)).thenReturn(Optional.empty());

		ResponseEntity<Quiz> response = coreService.updateLastPlayed(userId, quizId);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(quizRepository, times(1)).findByIdAndUserId(quizId, userId);
		verify(quizRepository, never()).save(any());
	}





}
