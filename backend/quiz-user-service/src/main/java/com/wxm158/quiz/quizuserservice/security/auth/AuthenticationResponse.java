package com.wxm158.quiz.quizuserservice.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wxm158.quiz.quizuserservice.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @Enumerated(EnumType.STRING)
    private Role role;
}
