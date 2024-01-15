package com.wxm158.quiz.quizclassservice.controller;


import com.wxm158.quiz.quizclassservice.model.dto.request.AddMembersRequest;
import com.wxm158.quiz.quizclassservice.model.dto.request.CreateClassroomRequest;
import com.wxm158.quiz.quizclassservice.model.dto.response.AddMembersResponse;
import com.wxm158.quiz.quizclassservice.model.dto.response.ClassroomResponse;
import com.wxm158.quiz.quizclassservice.model.entity.Classroom;
import com.wxm158.quiz.quizclassservice.model.entity.Member;
import com.wxm158.quiz.quizclassservice.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "api/v1/classroom")
@RestController
public class ClassroomController {

    private final ClassroomService classroomService;
    @PostMapping()
    public ResponseEntity<Classroom> createClass(@RequestBody CreateClassroomRequest request) {

        return classroomService.createClass(request);
    }

    @GetMapping()
    public List<ClassroomResponse> getClassrooms(@RequestHeader("x-auth-user-id") String userId) {
        System.out.println("logged in user id " + userId);
        return classroomService.getClassrooms();
    }


    @GetMapping("/{classroomId}")
    public ClassroomResponse getClassroom(@PathVariable Long classroomId) {
        return classroomService.getClassroom(classroomId);
    }

    @PostMapping("/{classroomId}")
    public ResponseEntity<Classroom> updateClassroomDetails(@PathVariable Long classroomId,
                                                     @RequestBody CreateClassroomRequest request) {
        return classroomService.updateClassroomDetails(classroomId, request);
    }

    @PostMapping("/{classroomId}/{member}")
    public ResponseEntity<Member> addOneMember(@PathVariable Long classroomId,
                                               @PathVariable Long member) {
        return classroomService.addOneMember(classroomId, member);
    }

    @PostMapping("/{classroomId}/members")
    public ResponseEntity<AddMembersResponse> addMembers(@PathVariable Long classroomId,
                                             @RequestBody AddMembersRequest request) {
        return classroomService.addMembers(classroomId, request);
    }

    @DeleteMapping("/{classroomId}/{memberId}")
    public ResponseEntity<Void> deleteMemberFromClassroom(@PathVariable Long classroomId,
                                                          @PathVariable Long memberId) {
        return classroomService.deleteMemberFromClassroom(classroomId, memberId);
    }

    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long classroomId) {
        return classroomService.deleteClassroom(classroomId);
    }

    @DeleteMapping("/member/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        return classroomService.deleteMember(id);
    }

}
