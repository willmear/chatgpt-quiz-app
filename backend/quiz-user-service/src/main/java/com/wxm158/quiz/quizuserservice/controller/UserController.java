package com.wxm158.quiz.quizuserservice.controller;

import com.wxm158.quiz.quizuserservice.model.dto.response.UserResponse;
import com.wxm158.quiz.quizuserservice.service.UserService;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/user")
    public UserResponse getUser(@RequestHeader("x-auth-user-id") String userId) {
        return userService.getUser(Long.valueOf(userId));
    }

    @GetMapping("/byIds")
    public List<UserResponse> getUsersByIds(@RequestParam List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/exist")
    public List<Long> checkUsersExist(@RequestParam List<Long> ids) {
        return userService.checkUsersExist(ids);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }


}
