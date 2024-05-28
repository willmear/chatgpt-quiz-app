package com.wxm158.quiz.quizclassservice;

import com.wxm158.quiz.quizclassservice.model.dto.request.CreateClassroomRequest;
import com.wxm158.quiz.quizclassservice.model.dto.response.AssignmentResponse;
import com.wxm158.quiz.quizclassservice.model.entity.Assignment;
import com.wxm158.quiz.quizclassservice.model.entity.AssignmentCompletion;
import com.wxm158.quiz.quizclassservice.model.entity.Classroom;
import com.wxm158.quiz.quizclassservice.model.entity.Member;
import com.wxm158.quiz.quizclassservice.repository.*;
import com.wxm158.quiz.quizclassservice.service.ClassroomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizClassServiceApplicationTests {

	@Mock
	private ClassroomRepository classroomRepository;

	@Mock
	private AssignmentCompletionRepository assignmentCompletionRepository;

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AssignmentRepository assignmentRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private ClassroomService classroomService;

	@Test
	void testCreateClass_GenerateUUID() {
		Long userId = 1L;
		CreateClassroomRequest classroomRequest = new CreateClassroomRequest("Math Class");

		Classroom savedClassroom = Classroom.builder()
				.id(1L)
				.name("Math Class")
				.adminId(userId)
				.createdAt(LocalDateTime.now())
				.joinCode("dummy-uuid")
				.build();
		when(classroomRepository.save(any())).thenReturn(savedClassroom);

		ResponseEntity<Classroom> responseEntity = classroomService.createClass(userId, classroomRequest);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
		assertNotNull(responseEntity.getBody().getJoinCode());
		assertFalse(responseEntity.getBody().getJoinCode().isEmpty());
	}


	@Test
	void testGetClassroomsIfOwned_Success() {
		Long userId = 1L;
		Classroom classroom1 = new Classroom(1L, "Math Class", userId, null, "join-code-1", null);
		Classroom classroom2 = new Classroom(2L, "Physics Class", userId, null, "join-code-2", null);
		List<Classroom> expectedClassrooms = List.of(classroom1, classroom2);

		when(classroomRepository.findClassroomsByAdminId(userId)).thenReturn(expectedClassrooms);

		List<Classroom> actualClassrooms = classroomService.getClassroomsIfOwned(userId);

		assertEquals(expectedClassrooms.size(), actualClassrooms.size());
		for (int i = 0; i < expectedClassrooms.size(); i++) {
			assertEquals(expectedClassrooms.get(i), actualClassrooms.get(i));
		}

		verify(classroomRepository, times(1)).findClassroomsByAdminId(userId);
	}

	@Test
	void testGetClassroomsIfOwned_NoClassroomsFound() {
		Long userId = 1L;

		when(classroomRepository.findClassroomsByAdminId(userId)).thenReturn(new ArrayList<>());

		List<Classroom> actualClassrooms = classroomService.getClassroomsIfOwned(userId);

		assertNotNull(actualClassrooms);
		assertTrue(actualClassrooms.isEmpty());

		verify(classroomRepository, times(1)).findClassroomsByAdminId(userId);
	}

	@Test
	void testGetClassroomsIfMember_Success() {
		Long userId = 1L;
		Classroom classroom1 = new Classroom(1L, "Math Class", null, null, "join-code-1", List.of(new Member(1L, userId)));
		Classroom classroom2 = new Classroom(2L, "Physics Class", null, null, "join-code-2", List.of(new Member(2L, userId)));
		List<Classroom> expectedClassrooms = List.of(classroom1, classroom2);

		when(classroomRepository.findClassroomsByMembersMemberId(userId)).thenReturn(expectedClassrooms);

		List<Classroom> actualClassrooms = classroomService.getClassroomsIfMember(userId);

		assertEquals(expectedClassrooms.size(), actualClassrooms.size());
		for (int i = 0; i < expectedClassrooms.size(); i++) {
			assertEquals(expectedClassrooms.get(i), actualClassrooms.get(i));
		}

		verify(classroomRepository, times(1)).findClassroomsByMembersMemberId(userId);
	}

	@Test
	void testGetClassroomsIfMember_NoClassroomsFound() {
		Long userId = 1L;

		when(classroomRepository.findClassroomsByMembersMemberId(userId)).thenReturn(new ArrayList<>());

		List<Classroom> actualClassrooms = classroomService.getClassroomsIfMember(userId);

		assertNotNull(actualClassrooms);
		assertTrue(actualClassrooms.isEmpty());

		verify(classroomRepository, times(1)).findClassroomsByMembersMemberId(userId);
	}

	@Test
	void testUpdateClassroomDetails_Success() {
		Long classroomId = 1L;
		Long userId = 1L;
		Classroom request = new Classroom();
		request.setName("Updated Classroom Name");
		Classroom existingClassroom = new Classroom(classroomId, "Original Classroom Name", userId, LocalDateTime.now(), "join-code", null);

		when(classroomRepository.findByIdAndAdminId(classroomId, userId)).thenReturn(Optional.of(existingClassroom));
		when(classroomRepository.save(existingClassroom)).thenReturn(existingClassroom);

		ResponseEntity<Classroom> response = classroomService.updateClassroomDetails(classroomId, request, userId);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(existingClassroom, response.getBody());
		assertEquals(request.getName(), response.getBody().getName());

		verify(classroomRepository, times(1)).findByIdAndAdminId(classroomId, userId);
		verify(classroomRepository, times(1)).save(existingClassroom);
	}

	@Test
	void testUpdateClassroomDetails_ClassroomNotFound() {
		Long classroomId = 1L;
		Long userId = 1L;
		Classroom request = new Classroom();

		when(classroomRepository.findByIdAndAdminId(classroomId, userId)).thenReturn(Optional.empty());

		ResponseEntity<Classroom> response = classroomService.updateClassroomDetails(classroomId, request, userId);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

		verify(classroomRepository, times(1)).findByIdAndAdminId(classroomId, userId);
		verify(classroomRepository, never()).save(any());
	}

	@Test
	void testAddMember_Success() {
		String joinCode = "join-code";
		Long userId = 1L;
		Classroom classroom = new Classroom(1L, "Classroom", userId, null, joinCode, null);
		Member newMember = new Member(1L, userId);

		when(classroomRepository.existsByJoinCodeAndAdminId(joinCode, userId)).thenReturn(false);
		when(classroomRepository.findClassroomByJoinCode(joinCode)).thenReturn(Optional.of(classroom));
		when(classroomRepository.existsClassroomByIdAndMembers_MemberId(classroom.getId(), userId)).thenReturn(false);

		when(memberRepository.save(any())).thenReturn(newMember);

		ResponseEntity<Member> response = classroomService.addMember(joinCode, userId);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(newMember, response.getBody());

		verify(classroomRepository, times(1)).existsByJoinCodeAndAdminId(joinCode, userId);
		verify(classroomRepository, times(1)).findClassroomByJoinCode(joinCode);
		verify(classroomRepository, times(1)).existsClassroomByIdAndMembers_MemberId(classroom.getId(), userId);
		verify(memberRepository, times(1)).save(any());
		verify(classroomRepository, times(1)).save(classroom);
	}

	@Test
	void testAddMember_JoinCodeNotFound() {
		String joinCode = "join-code";
		Long userId = 1L;

		when(classroomRepository.existsByJoinCodeAndAdminId(joinCode, userId)).thenReturn(false);
		when(classroomRepository.findClassroomByJoinCode(joinCode)).thenReturn(Optional.empty());

		ResponseEntity<Member> response = classroomService.addMember(joinCode, userId);

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		verify(classroomRepository, times(1)).existsByJoinCodeAndAdminId(joinCode, userId);
		verify(classroomRepository, times(1)).findClassroomByJoinCode(joinCode);
		verify(classroomRepository, never()).existsClassroomByIdAndMembers_MemberId(anyLong(), anyLong());
		verify(memberRepository, never()).save(any());
		verify(classroomRepository, never()).save(any());
	}

	@Test
	void testAddMember_AlreadyAMember() {
		String joinCode = "join-code";
		Long userId = 1L;
		Classroom classroom = new Classroom(1L, "Classroom", userId, null, joinCode, null);

		when(classroomRepository.existsByJoinCodeAndAdminId(joinCode, userId)).thenReturn(false);
		when(classroomRepository.findClassroomByJoinCode(joinCode)).thenReturn(Optional.of(classroom));
		when(classroomRepository.existsClassroomByIdAndMembers_MemberId(classroom.getId(), userId)).thenReturn(true);

		ResponseEntity<Member> response = classroomService.addMember(joinCode, userId);

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		verify(classroomRepository, times(1)).existsByJoinCodeAndAdminId(joinCode, userId);
		verify(classroomRepository, times(1)).findClassroomByJoinCode(joinCode);
		verify(classroomRepository, times(1)).existsClassroomByIdAndMembers_MemberId(classroom.getId(), userId);
		verify(classroomRepository, never()).save(any());
	}

	@Test
	void testDeleteClassroom_Success() {
		Long classroomId = 1L;
		Long adminId = 1L;

		ResponseEntity<Void> response = classroomService.deleteClassroom(classroomId, adminId);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

		verify(classroomRepository, times(1)).deleteByIdAndAdminId(classroomId, adminId);
	}

	@Test
	void testDeleteClassroom_NotAdmin() {
		Long classroomId = 1L;
		Long adminId = 1L;

		doNothing().when(classroomRepository).deleteByIdAndAdminId(classroomId, adminId + 1);

		ResponseEntity<Void> response = classroomService.deleteClassroom(classroomId, adminId + 1);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

		verify(classroomRepository, never()).deleteByIdAndAdminId(classroomId, adminId);
	}

	@Test
	void testDeleteMemberFromClassroom_Admin_Success() {
		Long classroomId = 1L;
		Long memberId = 1L;
		Long userId = 1L;

		Classroom classroom = Classroom.builder()
				.id(classroomId)
				.adminId(userId)
				.members(new ArrayList<>())
				.build();
		Member member = Member.builder()
				.id(memberId)
				.memberId(memberId)
				.build();
		when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

		ResponseEntity<Void> response = classroomService.deleteMemberFromClassroom(classroomId, memberId, userId);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertTrue(classroom.getMembers().isEmpty());

		verify(classroomRepository, times(1)).save(classroom);
		verify(memberRepository, never()).delete(any());
	}

	@Test
	void testDeleteMemberFromClassroom_NotAdmin_Failure() {
		Long classroomId = 1L;
		Long memberId = 1L;
		Long userId = 2L;

		Classroom classroom = Classroom.builder()
				.id(classroomId)
				.adminId(1L)
				.members(new ArrayList<>())
				.build();
		Member member = Member.builder()
				.id(memberId)
				.memberId(memberId)
				.build();
		when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

		ResponseEntity<Void> response = classroomService.deleteMemberFromClassroom(classroomId, memberId, userId);

		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

		verify(classroomRepository, never()).save(any());
		verify(memberRepository, never()).delete(any());
	}

	@Test
	void testGetClassroom_ValidId() {
		Long classroomId = 1L;
		Classroom expectedClassroom = Classroom.builder()
				.id(classroomId)
				.name("Test Classroom")
				.adminId(1L)
				.build();

		when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(expectedClassroom));

		Classroom result = classroomService.getClassroom(classroomId);

		assertNotNull(result);
		assertEquals(expectedClassroom, result);

		verify(classroomRepository, times(1)).findById(classroomId);
	}

	@Test
	void testGetClassroom_InvalidId() {
		Long invalidClassroomId = 999L;

		when(classroomRepository.findById(invalidClassroomId)).thenReturn(Optional.empty());

		Classroom result = classroomService.getClassroom(invalidClassroomId);

		assertNull(result);

		verify(classroomRepository, times(1)).findById(invalidClassroomId);
	}

	@Test
	void testGetAssignmentsTeacher_ValidData() {
		Long userId = 1L;
		Long classroomId = 1L;
		List<Assignment> expectedAssignments = new ArrayList<>();
		expectedAssignments.add(Assignment.builder().id(1L).name("Assignment 1").build());
		expectedAssignments.add(Assignment.builder().id(2L).name("Assignment 2").build());

		when(assignmentRepository.getAllByClassroomIdAndClassroomAdminId(classroomId, userId)).thenReturn(expectedAssignments);

		List<Assignment> result = classroomService.getAssignmentsTeacher(userId, classroomId);

		assertNotNull(result);
		assertEquals(expectedAssignments.size(), result.size());
		assertEquals(expectedAssignments, result);

		verify(assignmentRepository, times(1)).getAllByClassroomIdAndClassroomAdminId(classroomId, userId);
	}

	@Test
	void testGetAssignmentsTeacher_EmptyData() {
		Long userId = 1L;
		Long classroomId = 1L;

		when(assignmentRepository.getAllByClassroomIdAndClassroomAdminId(classroomId, userId)).thenReturn(new ArrayList<>());

		List<Assignment> result = classroomService.getAssignmentsTeacher(userId, classroomId);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(assignmentRepository, times(1)).getAllByClassroomIdAndClassroomAdminId(classroomId, userId);
	}

	@Test
	void testCreateAssignment_ValidData() {
		Assignment inputAssignment = Assignment.builder()
				.name("Assignment 1")
				.releaseTime(LocalDateTime.now())
				.deadline(LocalDateTime.now().plusDays(7))
				.questions(new ArrayList<>())
				.classroom(Classroom.builder().id(1L).name("Classroom 1").build())
				.timer(60.0)
				.multipleAttempts(false)
				.build();

		Assignment expectedAssignment = Assignment.builder()
				.id(1L)
				.name("Assignment 1")
				.releaseTime(inputAssignment.getReleaseTime())
				.deadline(inputAssignment.getDeadline())
				.questions(new ArrayList<>())
				.classroom(inputAssignment.getClassroom())
				.timer(60.0)
				.multipleAttempts(false)
				.build();

		when(questionRepository.saveAll(inputAssignment.getQuestions())).thenReturn(new ArrayList<>());

		when(assignmentRepository.save(any())).thenReturn(expectedAssignment);

		ResponseEntity<Assignment> result = classroomService.createAssignment(inputAssignment);

		assertNotNull(result);
		assertEquals(ResponseEntity.ok(expectedAssignment), result);

		verify(questionRepository, times(1)).saveAll(inputAssignment.getQuestions());
		verify(assignmentRepository, times(1)).save(any());
	}

	@Test
	void testCreateAssignment_MissingDeadline() {
		Assignment inputAssignment = Assignment.builder()
				.name("Assignment 1")
				.releaseTime(LocalDateTime.now())
				.questions(new ArrayList<>())
				.classroom(Classroom.builder().id(1L).name("Classroom 1").build())
				.timer(60.0)
				.multipleAttempts(false)
				.build();

		ResponseEntity<Assignment> result = classroomService.createAssignment(inputAssignment);

		assertNotNull(result);
		assertEquals(ResponseEntity.badRequest().build(), result);

		verify(questionRepository, never()).saveAll(any());
		verify(assignmentRepository, never()).save(any());
	}


	@Test
	void testGetAssignment() {
		Long assignmentId = 1L;
		Assignment expectedAssignment = Assignment.builder()
				.id(assignmentId)
				.name("Sample Assignment")
				.releaseTime(LocalDateTime.now())
				.deadline(LocalDateTime.now().plusDays(7))
				.build();

		when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(expectedAssignment));

		Assignment result = classroomService.getAssignment(assignmentId);

		assertNotNull(result);
		assertEquals(expectedAssignment, result);

		verify(assignmentRepository, times(1)).findById(assignmentId);
	}

	@Test
	void testGetAssignments() {
		Long userId = 1L;
		List<Assignment> expectedAssignments = new ArrayList<>();
		expectedAssignments.add(Assignment.builder().id(1L).name("Assignment 1").build());
		expectedAssignments.add(Assignment.builder().id(2L).name("Assignment 2").build());

		when(assignmentRepository.findAllByClassroomMembersMemberId(userId)).thenReturn(expectedAssignments);

		List<Assignment> result = classroomService.getAssignments(userId);

		assertNotNull(result);
		assertEquals(expectedAssignments.size(), result.size());
		assertEquals(expectedAssignments, result);

		verify(assignmentRepository, times(1)).findAllByClassroomMembersMemberId(userId);
	}

	@Test
	void testGetAssignmentsAdmin() {
		Long userId = 1L;
		List<Assignment> expectedAssignments = new ArrayList<>();
		expectedAssignments.add(Assignment.builder().id(1L).name("Assignment 1").build());
		expectedAssignments.add(Assignment.builder().id(2L).name("Assignment 2").build());

		when(assignmentRepository.findAllByClassroomAdminId(userId)).thenReturn(expectedAssignments);

		List<Assignment> result = classroomService.getAssignmentsAdmin(userId);

		assertNotNull(result);
		assertEquals(expectedAssignments.size(), result.size());
		assertEquals(expectedAssignments, result);

		verify(assignmentRepository, times(1)).findAllByClassroomAdminId(userId);
	}

	@Test
	void testGetAssignmentCompletionsAdmin() {
		Long userId = 1L;
		Long assignmentId = 1L;
		List<AssignmentCompletion> expectedCompletions = new ArrayList<>();
		expectedCompletions.add(AssignmentCompletion.builder().id(1L).completionTime(60).build());
		expectedCompletions.add(AssignmentCompletion.builder().id(2L).completionTime(45).build());

		when(assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentId(userId, assignmentId)).thenReturn(expectedCompletions);

		List<AssignmentCompletion> result = classroomService.getAssignmentCompletionsAdmin(userId, assignmentId);

		assertNotNull(result);
		assertEquals(expectedCompletions.size(), result.size());
		assertEquals(expectedCompletions, result);

		verify(assignmentCompletionRepository, times(1)).findAllByAssignmentClassroomAdminIdAndAssignmentId(userId, assignmentId);
	}

	@Test
	void testGetAssignmentCompletionsByAdminId() {
		Long userId = 1L;
		List<AssignmentCompletion> expectedCompletions = new ArrayList<>();
		expectedCompletions.add(AssignmentCompletion.builder().id(1L).completionTime(60).build());
		expectedCompletions.add(AssignmentCompletion.builder().id(2L).completionTime(45).build());

		when(assignmentCompletionRepository.findAllByAssignmentClassroomAdminId(userId)).thenReturn(expectedCompletions);

		List<AssignmentCompletion> result = classroomService.getAssignmentCompletionsByAdminId(userId);

		assertNotNull(result);
		assertEquals(expectedCompletions.size(), result.size());
		assertEquals(expectedCompletions, result);

		verify(assignmentCompletionRepository, times(1)).findAllByAssignmentClassroomAdminId(userId);
	}

	@Test
	void testGetAssignmentCompletionsForClass() {
		Long userId = 1L;
		Long classId = 1L;
		List<AssignmentCompletion> expectedCompletions = new ArrayList<>();
		expectedCompletions.add(AssignmentCompletion.builder().id(1L).completionTime(60).build());
		expectedCompletions.add(AssignmentCompletion.builder().id(2L).completionTime(45).build());

		when(assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classId))
				.thenReturn(expectedCompletions);

		List<AssignmentCompletion> result = classroomService.getAssignmentCompletionsForClass(userId, classId);

		assertNotNull(result);
		assertEquals(expectedCompletions.size(), result.size());
		assertEquals(expectedCompletions, result);

		verify(assignmentCompletionRepository, times(1)).findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classId);
	}

	@Test
	void testGetAllCompletionsMember() {
		Long userId = 1L;
		Long classId = 1L;
		Long memberId = 1L;
		List<AssignmentCompletion> expectedCompletions = new ArrayList<>();
		expectedCompletions.add(AssignmentCompletion.builder().id(1L).completionTime(60).build());
		expectedCompletions.add(AssignmentCompletion.builder().id(2L).completionTime(45).build());

		when(assignmentCompletionRepository.findAllByMemberMemberIdAndAssignmentClassroomIdAndAssignmentClassroomAdminId(memberId, classId, userId))
				.thenReturn(expectedCompletions);

		List<AssignmentCompletion> result = classroomService.getAllCompletionsMember(userId, classId, memberId);

		assertNotNull(result);
		assertEquals(expectedCompletions.size(), result.size());
		assertEquals(expectedCompletions, result);

		verify(assignmentCompletionRepository, times(1)).findAllByMemberMemberIdAndAssignmentClassroomIdAndAssignmentClassroomAdminId(memberId, classId, userId);
	}

	@Test
	void testGetRecentCompletionsForEachClassroomTeacher() {
		Long userId = 1L;
		Classroom classroom1 = Classroom.builder().id(1L).build();
		Classroom classroom2 = Classroom.builder().id(2L).build();
		List<Classroom> classrooms = List.of(classroom1, classroom2);

		List<List<AssignmentCompletion>> expectedCompletions = new ArrayList<>();
		List<AssignmentCompletion> completions1 = List.of(AssignmentCompletion.builder().id(1L).build());
		List<AssignmentCompletion> completions2 = List.of(AssignmentCompletion.builder().id(2L).build());
		expectedCompletions.add(completions1);
		expectedCompletions.add(completions2);

		when(classroomRepository.findClassroomsByAdminId(userId)).thenReturn(classrooms);
		when(assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classroom1.getId()))
				.thenReturn(completions1);
		when(assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classroom2.getId()))
				.thenReturn(completions2);

		List<List<AssignmentCompletion>> result = classroomService.getRecentCompletionsForEachClassroomTeacher(userId);

		assertNotNull(result);
		assertEquals(expectedCompletions.size(), result.size());
		assertEquals(expectedCompletions.get(0), result.get(0));
		assertEquals(expectedCompletions.get(1), result.get(1));

		verify(classroomRepository, times(1)).findClassroomsByAdminId(userId);
		verify(assignmentCompletionRepository, times(1)).findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classroom1.getId());
		verify(assignmentCompletionRepository, times(1)).findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classroom2.getId());
	}

	@Test
	void testGetCompletionsMemberAllClasses() {
		Long userId = 1L;
		List<AssignmentCompletion> expectedCompletions = List.of(
				AssignmentCompletion.builder().id(1L).build(),
				AssignmentCompletion.builder().id(2L).build()
		);

		when(assignmentCompletionRepository.findAllByMemberMemberId(userId, null)).thenReturn(expectedCompletions);

		List<AssignmentCompletion> result = classroomService.getCompletionsMemberAllClasses(userId);

		assertNotNull(result);
		assertEquals(expectedCompletions.size(), result.size());
		assertEquals(expectedCompletions, result);

		verify(assignmentCompletionRepository, times(1)).findAllByMemberMemberId(userId, null);
	}

	@Test
	void testGetAverageForAllAssignments() {
		Long userId = 1L;
		Long classId = 1L;
		List<AssignmentCompletion> completions = new ArrayList<>();
		Assignment assignment1 = Assignment.builder().id(1L).name("Assignment 1").build();
		AssignmentCompletion completion1 = AssignmentCompletion.builder().id(1L).assignment(assignment1).build();
		AssignmentCompletion completion2 = AssignmentCompletion.builder().id(2L).assignment(assignment1).build();
		completions.add(completion1);
		completions.add(completion2);

		when(assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classId))
				.thenReturn(completions);

		Map<String, Double> result = classroomService.getAverageForAllAssignments(userId, classId);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.containsKey("Assignment 1"));
	}



}
