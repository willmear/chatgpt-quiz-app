package com.wxm158.quiz.quizclassservice.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AddMembersResponse {
    private Long classroomId;
    private List<Long> membersAdded;
    private List<Long> members;
}
