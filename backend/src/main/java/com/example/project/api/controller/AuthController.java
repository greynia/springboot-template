package com.example.project.api.controller;

import com.example.project.api.dto.auth.ChangePasswordRequest;
import com.example.project.api.dto.auth.LoginRequest;
import com.example.project.api.dto.auth.LoginResponse;
import com.example.project.api.dto.auth.RefreshTokenRequest;
import com.example.project.api.dto.auth.UserProfileResponse;
import com.example.project.api.dto.common.UserSummaryResponse;
import com.example.project.application.auth.AuthResult;
import com.example.project.application.auth.AuthService;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.application.task.TaskMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
        return new LoginResponse(result.accessToken(), result.refreshToken(), user);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResult result = authService.refresh(request.refreshToken());
        UserSummaryResponse user = taskMapper.toUserSummary(result.user());
        return new LoginResponse(result.accessToken(), result.refreshToken(), user);
    }

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        AuthenticatedUser user = authService.getProfile(authenticatedUser.id());
        return new UserProfileResponse(user.id(), user.email(), user.name(), user.role());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        authService.logout(authenticatedUser.id());
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(authenticatedUser.id(), request.currentPassword(), request.newPassword());
    }
}
