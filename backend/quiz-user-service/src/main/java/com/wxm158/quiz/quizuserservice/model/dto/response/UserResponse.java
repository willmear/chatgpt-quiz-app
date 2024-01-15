package com.wxm158.quiz.quizuserservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstname;
    private String lastname;


}
