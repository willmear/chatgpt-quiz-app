package com.wxm158.quiz.quizclassservice.service;

import com.wxm158.quiz.quizclassservice.model.dto.request.AddMembersRequest;
import com.wxm158.quiz.quizclassservice.model.dto.request.CreateClassroomRequest;
import com.wxm158.quiz.quizclassservice.model.dto.response.AddMembersResponse;
import com.wxm158.quiz.quizclassservice.model.dto.response.ClassroomResponse;
import com.wxm158.quiz.quizclassservice.model.entity.Classroom;
import com.wxm158.quiz.quizclassservice.model.entity.Member;
import com.wxm158.quiz.quizclassservice.repository.ClassroomRepository;
import com.wxm158.quiz.quizclassservice.repository.MemberRepository;
import com.wxm158.quiz.quizclassservice.service.rest.UserServiceRestClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final MemberRepository memberRepository;
    @Autowired
    private final UserServiceRestClient userServiceRestClient;

    /*
    * Post CreateClassroomRequest
    * Classroom and classroom-member mapping tables inserted to
    * mapping table stores
    * */
    public ResponseEntity<Classroom> createClass(CreateClassroomRequest request) {

        Classroom classroom = new Classroom();
        BeanUtils.copyProperties(request, classroom);
        Classroom result = classroomRepository.save(classroom);
        for (Long l: userServiceRestClient.checkUsersExist(request.getMembers())) {
            Member member = Member.builder()
                    .classroomId(classroom.getId())
                    .memberId(l)
                    .build();
            memberRepository.save(member);
        }

        return ResponseEntity.ok(result);

    }

    /*
     * Get all Classrooms
     * Returns List of classrooms according to ClassroomResponse
     * Including List of all member IDs
     * */
    public List<ClassroomResponse> getClassrooms() {

        List<Classroom> classrooms = classroomRepository.findAll();
        List<ClassroomResponse> classroomResponses = new ArrayList<>();
        for (Classroom c: classrooms) {
            ClassroomResponse cr = new ClassroomResponse();
            BeanUtils.copyProperties(c, cr);
            cr.setMemberList(userServiceRestClient.getUsersByIds(memberRepository.findAllByClassroomId(cr.getId())));
            classroomResponses.add(cr);
        }

        return classroomResponses;
    }

    /*
     * Post update to Classroom details
     * Cannot change members here
     * */
    public ResponseEntity<Classroom> updateClassroomDetails(Long id, CreateClassroomRequest request) {

        return classroomRepository.findById(id)
                .map(classroom -> {
                    classroom.setName(request.getName());
                    classroom.setTopic(request.getTopic());
                    Classroom updatedClassroom = classroomRepository.save(classroom);
                    return ResponseEntity.ok(updatedClassroom);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * Post Add one member to a classroom
     * if member does not exist in classroom
     * */
    public ResponseEntity<Member> addOneMember(Long classroomId, Long member) {
        if (memberRepository.existsByClassroomIdAndMemberId(classroomId, member)) {
            Member m = Member.builder()
                    .classroomId(classroomId)
                    .memberId(member)
                    .build();
            Member result = memberRepository.save(m);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();

    }

    /*
     * Post Add multiple members to a class
     * if members do not exist
     * */
    public ResponseEntity<AddMembersResponse> addMembers(Long classroomId, AddMembersRequest request) {

        AddMembersResponse addMembersResponse = new AddMembersResponse();
        addMembersResponse.setClassroomId(classroomId);
        List<Long> membersAdded = new ArrayList<>();
        for (Long member: request.getMembers()) {
            if (!memberRepository.existsByClassroomIdAndMemberId(classroomId, member)) {
                Member newMember = Member.builder()
                        .memberId(member)
                        .classroomId(classroomId)
                        .build();
                memberRepository.save(newMember);
                membersAdded.add(newMember.getMemberId());
            }
        }
        addMembersResponse.setMembersAdded(membersAdded);
        addMembersResponse.setMembers(memberRepository.findAllByClassroomId(classroomId));

        return ResponseEntity.ok(addMembersResponse);
    }

    /*
     * Delete classroom from classroom and member tables
     * */
    @Transactional
    public ResponseEntity<Void> deleteClassroom(Long classroomId) {
        classroomRepository.deleteById(classroomId);
        memberRepository.deleteByClassroomId(classroomId);
        return ResponseEntity.noContent().build();
    }

    /*
     * Delete member from member table using member and classroom IDs
     * */
    @Transactional
    public ResponseEntity<Void> deleteMemberFromClassroom(Long classroomId, Long memberId) {
        memberRepository.deleteByClassroomIdAndMemberId(classroomId, memberId);
        return ResponseEntity.noContent().build();
    }

    public ClassroomResponse getClassroom(Long id) {
        Classroom classroom = classroomRepository.findById(id).orElse(null);
        if (classroom != null) {
            List<Long> members = memberRepository.findAllByClassroomId(classroom.getId());
        }


        return null;
    }

    @Transactional
    public ResponseEntity<Void> deleteMember(Long id) {

        memberRepository.deleteByMemberId(id);

        return ResponseEntity.noContent().build();
    }
}
