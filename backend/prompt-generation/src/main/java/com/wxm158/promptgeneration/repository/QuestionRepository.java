package com.wxm158.promptgeneration.repository;

import com.wxm158.promptgeneration.model.entity.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    boolean existsByQuestion(String question);

    List<Question> findAllByUserId(Long userId);

    boolean existsByQuestionAndQuestionType(String question, String questionType);


//    List<Question> findByTopicsInOrderByGeneratedAtDesc(Collection<List<String>> topics, Pageable pageable);

//    List<Question> findByTopicsContainingOrderByGeneratedAtDesc(String topic, Pageable pageable);

    List<Question> findAllByIdIn(Collection<Long> id);
}
