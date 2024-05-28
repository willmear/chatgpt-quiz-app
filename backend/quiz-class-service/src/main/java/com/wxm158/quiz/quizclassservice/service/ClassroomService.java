package com.wxm158.quiz.quizclassservice.service;

import com.wxm158.quiz.quizclassservice.model.dto.response.AssignmentResponse;
import com.wxm158.quiz.quizclassservice.model.entity.Answer;
import com.wxm158.quiz.quizclassservice.model.dto.request.AssignmentCompletionRequest;
import com.wxm158.quiz.quizclassservice.model.dto.request.CreateClassroomRequest;
import com.wxm158.quiz.quizclassservice.model.entity.*;
import com.wxm158.quiz.quizclassservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final MemberRepository memberRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuestionRepository questionRepository;
    private final AssignmentCompletionRepository assignmentCompletionRepository;
    private final AnswerRepository answerRepository;

    public ResponseEntity<Classroom> createClass(Long userId, CreateClassroomRequest classroom) {

        String joinCode = generateUUID();
        if (joinCode == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Classroom newClassroom = Classroom.builder()
                .name(classroom.getName())
                .adminId(userId)
                .createdAt(LocalDateTime.now())
                .joinCode(joinCode)
                .build();

        Classroom savedClassroom = classroomRepository.save(newClassroom);

        if (savedClassroom != null) {
            return ResponseEntity.ok(savedClassroom);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private String generateUUID() {

        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0,8);

        if (classroomRepository.findClassroomByJoinCode(uuid).isPresent()) {
            return generateUUID();
        } else {
            return uuid;
        }

    }

    /*
     * Get all Classrooms
     * */
    public List<Classroom> getClassroomsIfOwned(Long userId) {

        return classroomRepository.findClassroomsByAdminId(userId);

    }

    public List<Classroom> getClassroomsIfMember(Long userId) {

        return classroomRepository.findClassroomsByMembersMemberId(userId);

    }

    /*
     * Post update to Classroom details
     * Cannot change members here
     * */
    public ResponseEntity<Classroom> updateClassroomDetails(Long id, Classroom request, Long userId) {

        return classroomRepository.findByIdAndAdminId(id, userId)
                .map(classroom -> {
                    classroom.setName(request.getName());
                    Classroom updatedClassroom = classroomRepository.save(classroom);
                    return ResponseEntity.ok(updatedClassroom);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * Post Add one member to a classroom
     * if member does not exist in classroom
     * */
    public ResponseEntity<Member> addMember(String joinCode, Long userId) {

        if (classroomRepository.existsByJoinCodeAndAdminId(joinCode, userId)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Classroom> classroomOptional = classroomRepository.findClassroomByJoinCode(joinCode);
        if (classroomOptional.isPresent()) {
            Classroom classroom = classroomOptional.get();
            if (classroom.getMembers() == null) {
                classroom.setMembers(new ArrayList<>());
            }
            if (!classroomRepository.existsClassroomByIdAndMembers_MemberId(classroom.getId(), userId)) {

                Member newMember = Member.builder()
                        .memberId(userId)
                        .build();

                Member savedMember = memberRepository.save(newMember);

                classroom.getMembers().add(savedMember);
                classroomRepository.save(classroom);

                return ResponseEntity.ok(savedMember);
            }
        }

        return ResponseEntity.badRequest().build();

    }


    /*
     * Delete classroom from classroom and member tables
     * */
    @Transactional
    public ResponseEntity<Void> deleteClassroom(Long classroomId, Long adminId) {

        classroomRepository.deleteByIdAndAdminId(classroomId, adminId);
        return ResponseEntity.noContent().build();

    }

    /*
     * Delete member from member table using member and classroom IDs
     * */
    @Transactional
    public ResponseEntity<Void> deleteMemberFromClassroom(Long classroomId, Long memberId, Long userId) {

        Classroom classroom = classroomRepository.findById(classroomId).orElse(null);
        Member member = memberRepository.findById(memberId).orElse(null);
        if (Objects.equals(classroom.getAdminId(), userId)) {
            classroom.getMembers().remove(member);
            classroomRepository.save(classroom);
            log.info("deleted member: {} from classroom: {}", memberId, classroomId);
        } else {
            log.info("failed to delete member: {} from classroom: {}", memberId, classroomId);
        }
        return ResponseEntity.noContent().build();

    }

    public Classroom getClassroom(Long id) {

        return classroomRepository.findById(id).orElse(null);

    }


    public List<Assignment> getAssignmentsTeacher(Long userId, Long classroomId) {

        return assignmentRepository.getAllByClassroomIdAndClassroomAdminId(classroomId, userId);

    }

    public ResponseEntity<Assignment> createAssignment(Assignment assignment) {

        if (assignment.getDeadline() == null) {
            return ResponseEntity.badRequest().build();
        } else if (assignment.getReleaseTime() == null) {
            assignment.setReleaseTime(LocalDateTime.now());
        } else if (assignment.getName() == null) {
            assignment.setName("Untitled Assignment");
        }

        List<Question> questions = questionRepository.saveAll(assignment.getQuestions());

        Assignment newAssignment = Assignment.builder()
                .questions(questions)
                .releaseTime(assignment.getReleaseTime())
                .deadline(assignment.getDeadline())
                .name(assignment.getName())
                .classroom(assignment.getClassroom())
                .timer(assignment.getTimer())
                .multipleAttempts(assignment.getMultipleAttempts())
                .build();

        return ResponseEntity.ok(assignmentRepository.save(newAssignment));
    }

    public List<AssignmentResponse> getAssignmentsMember(Long userId, Long classroomId) {
        List<Assignment> assignments = assignmentRepository.getAllByClassroomIdAndClassroomMembersMemberId(classroomId, userId);
        List<AssignmentResponse> response = new ArrayList<>();
        for (Assignment assignment: assignments) {
            List<AssignmentCompletion> completions = assignmentCompletionRepository.findAllByAssignmentIdAndMemberMemberId(assignment.getId(), userId);
            AssignmentResponse assignmentResponse = AssignmentResponse.builder()
                    .assignment(assignment)
                    .completions(completions)
                    .build();
            response.add(assignmentResponse);
        }
        response.sort(Comparator.comparing((AssignmentResponse assignmentResponse) -> assignmentResponse.getAssignment().getDeadline()));
        return response;

    }

    public Assignment getAssignment(Long id) {

        return assignmentRepository.findById(id).orElse(null);

    }

    public ResponseEntity<AssignmentCompletion> submitAssignment(Long memberId, AssignmentCompletionRequest request) {

        Member member = classroomRepository.findByMemberIdAndClassId(memberId, request.getClassId());
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId()).orElse(null);

        if (!assignment.getMultipleAttempts() && assignmentCompletionRepository.existsByMemberAndAssignment(member, assignment)) {
            return ResponseEntity.badRequest().build();
        }

        List<Boolean> answersList = new ArrayList<>();

        List<Answer> requestAnswers = request.getAnswers();
        for (int i = 0; i< Objects.requireNonNull(assignment).getQuestions().size(); i++) {
            if (requestAnswers.size() <= i || requestAnswers.get(i) == null || requestAnswers.get(i).getAnswer().isEmpty()) {
                answersList.add(Boolean.FALSE);
            }
            else if (requestAnswers.get(i).getQuestionType().equals("Ordering")
                    || requestAnswers.get(i).getQuestionType().equals("Drag and Drop")) {

                if (requestAnswers.get(i).getAnswer().equals(assignment.getQuestions().get(i).getAnswer())) {
                    answersList.add(Boolean.TRUE);
                } else {
                    answersList.add(Boolean.FALSE);
                }

            } else if (requestAnswers.get(i).getQuestionType().equals("Fill The Blanks")) {
                if (requestAnswers.get(i).getAnswer().get(0) < assignment.getQuestions().get(i).getAnswer().size()) {
                    answersList.add(Boolean.TRUE);
                } else {
                    answersList.add(Boolean.FALSE);
                }
            }
//            MULTIPLE ANSWERS, MULTIPLE CHOICE, TRUE/FALSE
            else {
                boolean isCorrect = Boolean.FALSE;
                for (Integer j: assignment.getQuestions().get(i).getAnswer()) {
                    if (Objects.equals(j, requestAnswers.get(i).getAnswer().get(0))) {
                        isCorrect = Boolean.TRUE;
                        break;
                    }
                }
                answersList.add(isCorrect);
            }

        }

        List<Answer> answers = answerRepository.saveAll(request.getAnswers());

        AssignmentCompletion completion = AssignmentCompletion.builder()
                .member(member)
                .completionTime(request.getCompletionTime())
                .assignment(assignment)
                .answer(answersList)
                .answers(answers)
                .timeOver(request.isTimeOver())
                .submittedAt(LocalDateTime.now())
                .exitedBeforeFinished(request.isExitedBeforeFinished())
                .build();

        AssignmentCompletion savedCompletion = assignmentCompletionRepository.save(completion);

        return ResponseEntity.ok(savedCompletion);
    }

    public List<Assignment> getAssignments(Long userId) {

        return assignmentRepository.findAllByClassroomMembersMemberId(userId);

    }

    public List<Assignment> getAssignmentsAdmin(Long userId) {

        return assignmentRepository.findAllByClassroomAdminId(userId);

    }

    public List<AssignmentCompletion> getAssignmentCompletionsAdmin(Long userId, Long assignmentId) {

        return assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentId(userId, assignmentId);

    }

    public List<AssignmentCompletion> getAssignmentCompletionsByAdminId(Long userId) {

        return assignmentCompletionRepository.findAllByAssignmentClassroomAdminId(userId);

    }

    public List<AssignmentCompletion> getAssignmentCompletionsForClass(Long userId, Long classId) {

        return assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classId);

    }

    public List<AssignmentCompletion> getAllCompletionsMember(Long userId, Long classId, Long memberId) {
        return assignmentCompletionRepository.findAllByMemberMemberIdAndAssignmentClassroomIdAndAssignmentClassroomAdminId(memberId, classId, userId);
    }

    public List<AssignmentCompletion> getRecentCompletions(Long userId) {

        Pageable pageable = PageRequest.of(0, 5, Sort.by("submittedAt").descending());

        return assignmentCompletionRepository.findAllByMemberMemberId(userId,pageable);

    }

    public List<List<AssignmentCompletion>> getRecentCompletionsForEachClassroom(Long userId) {

        List<Classroom> classrooms = classroomRepository.findClassroomByMembersMemberId(userId);

        List<List<AssignmentCompletion>> assignmentCompletions = new ArrayList<>();

        for (Classroom classroom: classrooms) {

            Pageable pageable = PageRequest.of(0, 5, Sort.by("submittedAt").descending());

            assignmentCompletions.add(assignmentCompletionRepository.findAllByMemberMemberIdAndAssignmentClassroomId(userId, classroom.getId(), pageable));

        }

        return assignmentCompletions;

    }

    public List<List<AssignmentCompletion>> getRecentCompletionsForEachClassroomTeacher(Long userId) {

        List<Classroom> classrooms = classroomRepository.findClassroomsByAdminId(userId);

        List<List<AssignmentCompletion>> assignmentCompletions = new ArrayList<>();

        for (Classroom classroom: classrooms) {

            assignmentCompletions.add(assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classroom.getId()));

        }

        return assignmentCompletions;

    }

    public List<AssignmentCompletion> getCompletionsMemberAllClasses(Long userId) {

        return assignmentCompletionRepository.findAllByMemberMemberId(userId, null);

    }

    public Map<String, Double> getAverageForAllAssignments(Long userId, Long classId) {
        List<AssignmentCompletion> completions =
                assignmentCompletionRepository.findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(userId, classId);

        Map<Assignment, List<AssignmentCompletion>> completionsByAssignment = completions.stream()
                .collect(Collectors.groupingBy(AssignmentCompletion::getAssignment));

        Map<String, Double> averageScoresByAssignment = new HashMap<>();
        completionsByAssignment.forEach((assignment, completionList) -> {
            double totalScore = completionList.stream()
                    .mapToDouble(this::calculateScore)
                    .sum();
            double averageScore = totalScore / completionList.size();
            averageScoresByAssignment.put(assignment.getName(), averageScore);
        });

        return averageScoresByAssignment;

    }

    private double calculateScore(AssignmentCompletion completion) {
        List<Boolean> answers = completion.getAnswer();
        if (answers == null) {
            return 0.0; // Return 0 if answers are null
        }
        double totalCorrectAnswers = answers.stream().filter(Boolean::booleanValue).count();
        double totalQuestions = completion.getAssignment().getQuestions().size();

        return totalQuestions == 0 ? 0.0 : (totalCorrectAnswers / totalQuestions) * 100.0;
    }


}
