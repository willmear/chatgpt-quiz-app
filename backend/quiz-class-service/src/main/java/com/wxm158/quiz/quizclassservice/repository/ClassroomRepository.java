package com.wxm158.quiz.quizclassservice.repository;

import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import com.netflix.appinfo.ApplicationInfoManager;
import com.wxm158.quiz.quizclassservice.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.wxm158.quiz.quizclassservice.model.entity.Classroom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {


    Optional<Classroom> findClassroomByJoinCode(String uuid);

    List<Classroom> findClassroomsByAdminId(Long userId);

    @Query("SELECT c FROM Classroom c JOIN c.members m WHERE m.memberId = :memberId")
    List<Classroom> findClassroomsByMembersMemberId(@Param("memberId") Long memberId);

    Optional<Classroom> findByIdAndAdminId(Long id, Long userId);

//    @Modifying
//    @Query("DELETE FROM Member m WHERE m.memberId = :memberId AND m.classroom.id = :classroomId")
//    void deleteMemberFromClassroom(@Param("classroomId") Long classroomId, @Param("memberId") Long memberId);

    Boolean existsClassroomByIdAndMembers_MemberId(Long classroomId, Long memberId);
    @Modifying
    void deleteByIdAndAdminId(Long classroomId, Long adminId);

    boolean existsByJoinCodeAndAdminId(String joinCode, Long userId);

    @Query("SELECT m FROM Classroom c JOIN c.members m WHERE m.memberId = :memberId AND c.id = :classId")
    Member findByMemberIdAndClassId(@Param("memberId") Long memberId, @Param("classId") Long classId);


    List<Classroom> findClassroomByMembersMemberId(Long userId);
}
