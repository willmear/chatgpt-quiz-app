package com.wxm158.quiz.quizuserservice;

import com.wxm158.quiz.quizuserservice.enums.Role;
import com.wxm158.quiz.quizuserservice.model.dto.response.UserResponse;
import com.wxm158.quiz.quizuserservice.model.entity.User;
import com.wxm158.quiz.quizuserservice.repository.UserRepository;
import com.wxm158.quiz.quizuserservice.security.token.TokenRepository;
import com.wxm158.quiz.quizuserservice.service.UserService;
import com.wxm158.quiz.quizuserservice.service.rest.ClassroomServiceRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private TokenRepository tokenRepository;

	@Mock
	private ClassroomServiceRestClient classroomServiceRestClient;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		User user = new User(1L, "John", "Doe", "john@example.com", "password", "School", Role.TEACHER);
	}

	@Test
	void testGetUserById() {
		User user = new User(1L, "John", "Doe", "john@example.com", "password", "School", Role.TEACHER);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		UserResponse userResponse = userService.getUserById(1L);

		assertNotNull(userResponse);
		assertEquals("John", userResponse.getFirstname());
		assertEquals("Doe", userResponse.getLastname());
	}


	@Test
	void testGetUserById_NotFound() {
		UserResponse userResponse = userService.getUserById(2L);
		assertEquals(null, userResponse);
	}

	@Test
	void testGetUsersByIds() {
		List<Long> userIds = List.of(1L);
		List<User> users = new ArrayList<>();
		users.add(new User(1L, "John", "Doe", "john@example.com", "password", "School", Role.TEACHER));

		when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));

		List<UserResponse> userResponses = userService.getUsersByIds(userIds);

		assertEquals(1, userResponses.size());
		assertEquals("John", userResponses.get(0).getFirstname());
		assertEquals("Doe", userResponses.get(0).getLastname());
	}


	@Test
	void testCheckUsersExist() {
		when(userRepository.existsById(1L)).thenReturn(true);
		when(userRepository.existsById(2L)).thenReturn(false);

		List<Long> existingIds = userService.checkUsersExist(List.of(1L, 2L));
		assertEquals(1, existingIds.size());
		assertEquals(1L, existingIds.get(0));
	}

	@Test
	void testDeleteUser() {
		User user = new User(1L, "John", "Doe", "john@example.com", "password", "School", Role.TEACHER);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		ResponseEntity<Void> responseEntity = userService.deleteUser(1L);

		assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
		verify(tokenRepository, times(1)).deleteByUser(Optional.of(user)); // Properly verifying the invocation
		verify(userRepository, times(1)).deleteById(1L);
		verify(classroomServiceRestClient, times(1)).deleteMemberFromClassroom(1L);
	}



	@Test
	void testDeleteUser_NotFound() {
		userService.deleteUser(2L);
		verify(tokenRepository, never()).deleteByUser(any());
		verify(userRepository, never()).deleteById(anyLong());
		verify(classroomServiceRestClient, never()).deleteMemberFromClassroom(anyLong());
	}

	@Test
	void testGetUser() {
		User user = new User(1L, "John", "Doe", "john@example.com", "password", "School", Role.TEACHER);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		UserResponse userResponse = userService.getUser(1L);
		assertEquals("John", userResponse.getFirstname());
		assertEquals("Doe", userResponse.getLastname());
	}
}