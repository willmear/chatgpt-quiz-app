package com.wxm158.quiz.quizclassservice.model.dto.request;

import com.wxm158.quiz.quizclassservice.model.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentCompletionRequest {

    private Long assignmentId;
    private Long classId;
    private Integer completionTime;
    private List<Answer> answers;
    private boolean timeOver;
    private boolean exitedBeforeFinished;

}
