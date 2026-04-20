package com.example.project.api.controller;

import com.example.project.api.dto.common.UserSummaryResponse;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.application.user.UserQueryService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryService userQueryService;

    public UserController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping
    public List<UserSummaryResponse> listUsers(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return userQueryService.listActiveUsers(authenticatedUser);
    }
}
