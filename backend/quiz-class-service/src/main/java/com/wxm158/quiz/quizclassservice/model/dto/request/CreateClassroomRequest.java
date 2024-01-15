package com.wxm158.quiz.quizclassservice.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateClassroomRequest {
    private String name;
    private String topic;
    private String createdAt;
    private List<Long> members;
}
