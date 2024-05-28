package com.wxm158.quiz.quizclassservice.controller;

import com.wxm158.quiz.quizclassservice.model.dto.request.AddMembersRequest;
import com.wxm158.quiz.quizclassservice.model.dto.request.AssignmentCompletionRequest;
import com.wxm158.quiz.quizclassservice.model.dto.request.CreateClassroomRequest;
import com.wxm158.quiz.quizclassservice.model.dto.response.AddMembersResponse;
import com.wxm158.quiz.quizclassservice.model.dto.response.AssignmentResponse;
import com.wxm158.quiz.quizclassservice.model.dto.response.ClassroomResponse;
import com.wxm158.quiz.quizclassservice.model.entity.Assignment;
import com.wxm158.quiz.quizclassservice.model.entity.AssignmentCompletion;
import com.wxm158.quiz.quizclassservice.model.entity.Classroom;
import com.wxm158.quiz.quizclassservice.model.entity.Member;
import com.wxm158.quiz.quizclassservice.service.ClassroomService;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping(value = "api/v1/classroom")
@RestController
public class ClassroomController {

    private final ClassroomService classroomService;

    @PostMapping("/create")
    public ResponseEntity<Classroom> createClass(@RequestHeader("x-auth-user-id") String userId,
                                                 @RequestBody CreateClassroomRequest classroom) {

        return classroomService.createClass(Long.valueOf(userId), classroom);
    }

    @GetMapping("/owned")
    public List<Classroom> getClassroomsIfOwned(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getClassroomsIfOwned(Long.valueOf(userId));
    }

    @GetMapping("/member")
    public List<Classroom> getClassroomsIfMember(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getClassroomsIfMember(Long.valueOf(userId));
    }


    @GetMapping("/{classroomId}")
    public Classroom getClassroom(@PathVariable Long classroomId) {
        return classroomService.getClassroom(classroomId);
    }

    @PutMapping("/{classroomId}")
    public ResponseEntity<Classroom> updateClassroomDetails(@PathVariable Long classroomId,
                                                            @RequestBody Classroom request,
                                                            @RequestHeader("x-auth-user-id") String userId) {
        return classroomService.updateClassroomDetails(classroomId, request, Long.valueOf(userId));
    }

    @PostMapping("/join")
    public ResponseEntity<Member> addMember(@RequestBody String joinCode,
                                            @RequestHeader("x-auth-user-id") String userId) {
        return classroomService.addMember(joinCode, Long.valueOf(userId));
    }

    @DeleteMapping("/{classroomId}/{memberId}")
    public ResponseEntity<Void> deleteMemberFromClassroom(@PathVariable Long classroomId,
                                                          @PathVariable Long memberId,
                                                          @RequestHeader("x-auth-user-id") String userId) {
        return classroomService.deleteMemberFromClassroom(classroomId, memberId, Long.valueOf(userId));
    }

    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long classroomId,
                                                @RequestHeader("x-auth-user-id") String userId) {
        return classroomService.deleteClassroom(classroomId, Long.valueOf(userId));
    }

    @GetMapping("/admin/assignments/{classroomId}")
    public List<Assignment> getAssignmentsTeacher(@RequestHeader("x-auth-user-id") String userId,
                                           @PathVariable Long classroomId) {
        return classroomService.getAssignmentsTeacher(Long.valueOf(userId), classroomId);
    }

    @GetMapping("/member/assignments/{classroomId}")
    public List<AssignmentResponse> getAssignmentsMember(@RequestHeader("x-auth-user-id") String userId,
                                                         @PathVariable Long classroomId) {
        return classroomService.getAssignmentsMember(Long.valueOf(userId), classroomId);
    }

    @GetMapping("/assignment/{id}")
    public Assignment getAssignment(@PathVariable Long id) {
        return classroomService.getAssignment(id);
    }

    @PostMapping("/assign")
    public ResponseEntity<Assignment> createAssignment(@RequestBody Assignment assignment) {
        return classroomService.createAssignment(assignment);
    }

    @PostMapping("/submit")
    public ResponseEntity<AssignmentCompletion> submitAssignment(@RequestHeader("x-auth-user-id") String userId,
                                                                 @RequestBody AssignmentCompletionRequest request) {
        return classroomService.submitAssignment(Long.valueOf(userId), request);
    }

    @GetMapping("/member/assignments")
    public List<Assignment> getAssignments(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getAssignments(Long.valueOf(userId));
    }

    @GetMapping("/admin/assignments")
    public List<Assignment> getAssignmentsAdmin(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getAssignmentsAdmin(Long.valueOf(userId));
    }

    @GetMapping("/admin/completions")
    public List<AssignmentCompletion> getAssignmentCompletionsByAdminId(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getAssignmentCompletionsByAdminId(Long.valueOf(userId));
    }

    @GetMapping("/admin/completions/{assignmentId}")
    public List<AssignmentCompletion> getAssignmentCompletionsAdmin(@RequestHeader("x-auth-user-id") String userId,
                                                                    @PathVariable Long assignmentId) {
        return classroomService.getAssignmentCompletionsAdmin(Long.valueOf(userId), assignmentId);
    }

    @GetMapping("/admin/{classId}/completions")
    public List<AssignmentCompletion> getAssignmentCompletionsForClass(@RequestHeader("x-auth-user-id") String userId,
                                                                             @PathVariable Long classId) {
        return classroomService.getAssignmentCompletionsForClass(Long.valueOf(userId), classId);
    }

    @GetMapping("/admin/{classId}/{memberId}/completions")
    public List<AssignmentCompletion> getAllCompletionsMember(@RequestHeader("x-auth-user-id") String userId,
                                                              @PathVariable Long classId, @PathVariable Long memberId) {
        return classroomService.getAllCompletionsMember(Long.valueOf(userId), classId, memberId);
    }

    @GetMapping("/member/completions")
    public List<AssignmentCompletion> getRecentCompletions(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getRecentCompletions(Long.valueOf(userId));
    }

    @GetMapping("/member/completions/classroom")
    public List<List<AssignmentCompletion>> getRecentCompletionsForEachClassroom(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getRecentCompletionsForEachClassroom(Long.valueOf(userId));
    }

    @GetMapping("/admin/completions/classroom")
    public List<List<AssignmentCompletion>> getCompletionsForEachClassroomTeacher(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getRecentCompletionsForEachClassroomTeacher(Long.valueOf(userId));
    }

    @GetMapping("/member/completions/all")
    public List<AssignmentCompletion> getCompletionsMemberAllClasses(@RequestHeader("x-auth-user-id") String userId) {
        return classroomService.getCompletionsMemberAllClasses(Long.valueOf(userId));
    }

    @GetMapping("/admin/average/classroom/{classId}")
    public Map<String, Double> getAverageForAllAssignments(@RequestHeader("x-auth-user-id") String userId,
                                                               @PathVariable Long classId) {
        return classroomService.getAverageForAllAssignments(Long.valueOf(userId), classId);
    }
}
