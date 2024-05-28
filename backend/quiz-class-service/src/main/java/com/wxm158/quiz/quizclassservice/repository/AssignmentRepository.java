package com.wxm158.quiz.quizclassservice.repository;

import com.wxm158.quiz.quizclassservice.model.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> getAllByClassroomIdAndClassroomAdminId(Long classroomId, Long userId);

    @Query("SELECT a FROM Assignment a " +
            "JOIN a.classroom.members m " +
            "LEFT JOIN AssignmentCompletion ac ON a.id = ac.assignment.id AND ac.member.memberId = :memberId " +
            "WHERE a.classroom.id = :classroomId ")
    List<Assignment> getAllByClassroomIdAndClassroomMembersMemberId(
            @Param("classroomId") Long classroomId,
            @Param("memberId") Long memberId
    );

    List<Assignment> findAllByClassroomMembersMemberId(Long userId);

    List<Assignment> findAllByClassroomAdminId(Long userId);
}
