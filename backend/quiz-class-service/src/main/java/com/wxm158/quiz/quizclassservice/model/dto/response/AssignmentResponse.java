package com.wxm158.quiz.quizclassservice.model.dto.response;

import com.wxm158.quiz.quizclassservice.model.entity.Assignment;
import com.wxm158.quiz.quizclassservice.model.entity.AssignmentCompletion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentResponse {
    private Assignment assignment;
    private List<AssignmentCompletion> completions;
}
