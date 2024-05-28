package com.wxm158.quiz.quizclassservice.repository;

import com.wxm158.quiz.quizclassservice.model.entity.Assignment;
import com.wxm158.quiz.quizclassservice.model.entity.AssignmentCompletion;
import com.wxm158.quiz.quizclassservice.model.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentCompletionRepository extends JpaRepository<AssignmentCompletion, Long> {
    boolean existsByMemberAndAssignment(Member member, Assignment assignment);

    List<AssignmentCompletion> findAllByAssignmentClassroomAdminIdAndAssignmentId(Long userId, Long assignmentId);

    List<AssignmentCompletion> findAllByAssignmentIdAndMemberMemberId(Long assignmentId, Long memberId);

    List<AssignmentCompletion> findAllByAssignmentClassroomAdminIdAndAssignmentClassroomId(Long userId, Long classId);

    List<AssignmentCompletion> findAllByAssignmentClassroomAdminId(Long userId);

    List<AssignmentCompletion> findAllByMemberMemberIdAndAssignmentClassroomIdAndAssignmentClassroomAdminId(Long memberId, Long classId, Long userId);

    List<AssignmentCompletion> findAllByMemberMemberId(Long userId, Pageable pageable);


    List<AssignmentCompletion> findAllByMemberMemberIdAndAssignmentClassroomId(Long userId, Long classId, Pageable pageable);
}
