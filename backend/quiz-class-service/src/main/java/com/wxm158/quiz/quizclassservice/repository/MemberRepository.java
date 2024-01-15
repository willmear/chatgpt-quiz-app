package com.wxm158.quiz.quizclassservice.repository;

import com.wxm158.quiz.quizclassservice.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select memberId from Member where classroomId=:id")
    List<Long> findAllByClassroomId(@Param("id") Long id);

    @Query("select count(e) > 0 from Member e where e.memberId=:memberId and e.classroomId=:classroomId")
    Boolean existsByClassroomIdAndMemberId(@Param("classroomId") Long classroomId, @Param("memberId") Long memberId);

    void deleteByClassroomId(Long classroomId);

    @Modifying
    @Query("delete from Member e where e.classroomId=:classroomId and e.memberId=:memberId")
    void deleteByClassroomIdAndMemberId(@Param("classroomId") Long classroomId, @Param("memberId") Long memberId);

    void deleteByMemberId(Long id);
}
