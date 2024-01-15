package com.wxm158.quiz.quizclassservice.service.rest;

import com.wxm158.quiz.quizclassservice.model.rest.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "quiz-user-service")
public interface UserServiceRestClient {

    @GetMapping("api/v1/user/byIds")
    List<UserResponse> getUsersByIds(@RequestParam List<Long> ids);

    @GetMapping("api/v1/user/exist")
    List<Long> checkUsersExist(@RequestParam List<Long> ids);
}
