package com.wxm158.quiz.quizuserservice.service.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "quiz-class-service")
public interface ClassroomServiceRestClient {

    @DeleteMapping("api/v1/classroom/member/{id}")
    ResponseEntity<Void> deleteMemberFromClassroom(@PathVariable Long id);
}
