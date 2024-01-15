package com.wxm158.quiz.quizclassservice.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddMembersRequest {
    private List<Long> members;
}
