package com.example.project.application.auth;

import com.example.project.application.exception.BadRequestApplicationException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

    private final PasswordPolicyProperties properties;

    public PasswordPolicyValidator(PasswordPolicyProperties properties) {
        this.properties = properties;
    }

    public void validate(String password) {
        List<String> violations = new ArrayList<>();

        if (password.length() < properties.minLength()) {
            violations.add("Password must be at least " + properties.minLength() + " characters long.");
        }
        if (properties.requireUppercase() && password.chars().noneMatch(Character::isUpperCase)) {
            violations.add("Password must contain at least one uppercase letter.");
        }
        if (properties.requireLowercase() && password.chars().noneMatch(Character::isLowerCase)) {
            violations.add("Password must contain at least one lowercase letter.");
        }
        if (properties.requireDigit() && password.chars().noneMatch(Character::isDigit)) {
            violations.add("Password must contain at least one digit.");
        }
        if (properties.requireSpecialCharacter() && password.chars().allMatch(Character::isLetterOrDigit)) {
            violations.add("Password must contain at least one special character.");
        }

        if (!violations.isEmpty()) {
            throw new BadRequestApplicationException("PASSWORD_POLICY_VIOLATION", String.join(" ", violations));
        }
    }
}
