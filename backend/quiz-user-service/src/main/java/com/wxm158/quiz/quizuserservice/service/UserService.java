package com.wxm158.quiz.quizuserservice.service;

import com.wxm158.quiz.quizuserservice.model.dto.response.UserResponse;
import com.wxm158.quiz.quizuserservice.model.entity.User;
import com.wxm158.quiz.quizuserservice.repository.UserRepository;
import com.wxm158.quiz.quizuserservice.security.token.TokenRepository;
import com.wxm158.quiz.quizuserservice.service.rest.ClassroomServiceRestClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ClassroomServiceRestClient classroomServiceRestClient;

    public UserResponse getUserById(Long id) {

        return userRepository.findById(id)
                .map(user -> new UserResponse(user.getId(), user.getFirstname(), user.getLastname()))
                .orElse(null);

    }

    public List<UserResponse> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getFirstname(), user.getLastname()))
                .collect(Collectors.toList());

    }

    public List<Long> checkUsersExist(List<Long> ids) {
        List<Long> users = new ArrayList<>();
        for (long l: ids) {
            if (userRepository.existsById(l)) {
                users.add(l);
            }
        }
        return users;
    }

    @Transactional
    public ResponseEntity<Void> deleteUser(Long id) {

        if (userRepository.findById(id).isPresent()) {
            tokenRepository.deleteByUser(userRepository.findById(id));
            userRepository.deleteById(id);
            classroomServiceRestClient.deleteMemberFromClassroom(id);
        }

        return ResponseEntity.noContent().build();
    }
}
