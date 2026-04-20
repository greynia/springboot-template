package com.example.project.application.user;

import com.example.project.api.dto.common.UserSummaryResponse;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.application.exception.ForbiddenApplicationException;
import com.example.project.common.enums.UserRole;
import com.example.project.infrastructure.persistence.jpa.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserQueryService {

    private final UserRepository userRepository;

    public UserQueryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> listActiveUsers(AuthenticatedUser actor) {
        if (actor.role() != UserRole.ADMIN) {
            throw new ForbiddenApplicationException("USER_LIST_FORBIDDEN", "Only admins can list users.");
        }
        return userRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(u -> new UserSummaryResponse(u.getId(), u.getEmail(), u.getName(), u.getRole()))
                .toList();
    }
}
