package com.wxm158.quiz.quizclassservice.model.dto.response;

import com.wxm158.quiz.quizclassservice.model.entity.Member;
import com.wxm158.quiz.quizclassservice.model.rest.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomResponse {
    private Long id;
    private String name;
    private String topic;
    private LocalDate createdAt;
    private List<UserResponse> memberList;
}
