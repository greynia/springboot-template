package com.example.project.api.controller;

import com.example.project.api.dto.auth.LoginRequest;
import com.example.project.api.dto.auth.LoginResponse;
import com.example.project.api.dto.auth.UserProfileResponse;
import com.example.project.api.dto.common.UserSummaryResponse;
import com.example.project.application.auth.AuthResult;
import com.example.project.application.auth.AuthService;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.application.task.TaskMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TaskMapper taskMapper;

    public AuthController(AuthService authService, TaskMapper taskMapper) {
        this.authService = authService;
        this.taskMapper = taskMapper;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request.email(), request.password());
        UserSummaryResponse user = taskMapper.toUserSummary(result.user());
        return new LoginResponse(result.accessToken(), user);
    }

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        AuthenticatedUser user = authService.getProfile(authenticatedUser.id());
        return new UserProfileResponse(user.id(), user.email(), user.name(), user.role());
    }
}
