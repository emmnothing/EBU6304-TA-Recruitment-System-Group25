package com.bupt.ta.service;

import com.bupt.ta.dto.OperationResult;
import com.bupt.ta.dto.RegisterForm;
import com.bupt.ta.dto.ResetPasswordForm;
import com.bupt.ta.model.Role;
import com.bupt.ta.model.User;
import com.bupt.ta.repository.UserRepository;
import com.bupt.ta.util.IdGenerator;
import com.bupt.ta.util.PasswordUtil;
import com.bupt.ta.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();

    public OperationResult<User> authenticateUser(String username, String password, Role role) {
        if (ValidationUtil.isBlank(username) || ValidationUtil.isBlank(password) || role == null) {
            return OperationResult.failure("Please enter username, password, and role.");
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        Optional<User> matchedUser = userRepository.findAll()
            .stream()
            .filter(user -> user.getRole() == role)
            .filter(user -> user.getUsername().equalsIgnoreCase(username.trim()))
            .filter(user -> user.getPasswordHash().equals(passwordHash))
            .findFirst();

        return matchedUser
            .map(user -> OperationResult.success("Login successful.", user))
            .orElseGet(() -> OperationResult.failure("Incorrect username, password, or role."));
    }

    public OperationResult<User> registerUser(RegisterForm registerForm) {
        if (ValidationUtil.isBlank(registerForm.getUsername())
            || ValidationUtil.isBlank(registerForm.getEmail())
            || ValidationUtil.isBlank(registerForm.getPhoneNumber())
            || ValidationUtil.isBlank(registerForm.getPassword())) {
            return OperationResult.failure("Please complete all registration fields.");
        }
        if (!ValidationUtil.isValidEmail(registerForm.getEmail())) {
            return OperationResult.failure("Please enter a valid email address.");
        }
        if (!ValidationUtil.isValidPhoneNumber(registerForm.getPhoneNumber())) {
            return OperationResult.failure("Phone number must be exactly 11 digits.");
        }
        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            return OperationResult.failure("The two passwords do not match.");
        }

        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(registerForm.getUsername().trim())) {
                return OperationResult.failure("Username already exists.");
            }
            if (user.getEmail().equalsIgnoreCase(registerForm.getEmail().trim())) {
                return OperationResult.failure("Email already exists.");
            }
            if (user.getPhoneNumber().equals(registerForm.getPhoneNumber().trim())) {
                return OperationResult.failure("Phone number already exists.");
            }
        }

        User user = new User(
            IdGenerator.generateId("user"),
            registerForm.getUsername().trim(),
            registerForm.getEmail().trim(),
            registerForm.getPhoneNumber().trim(),
            PasswordUtil.hashPassword(registerForm.getPassword()),
            Role.TA_APPLICANT,
            LocalDateTime.now().toString()
        );

        users.add(user);
        userRepository.saveAll(users);
        return OperationResult.success("Registration successful. Please login as a TA Applicant.", user);
    }

    public OperationResult<User> resetPassword(ResetPasswordForm resetPasswordForm) {
        if (!ValidationUtil.isValidPhoneNumber(resetPasswordForm.getPhoneNumber())) {
            return OperationResult.failure("Phone number must be exactly 11 digits.");
        }
        if (!ValidationUtil.isValidEmail(resetPasswordForm.getEmail())) {
            return OperationResult.failure("Please enter a valid email address.");
        }
        if (ValidationUtil.isBlank(resetPasswordForm.getNewPassword())
            || !resetPasswordForm.getNewPassword().equals(resetPasswordForm.getConfirmPassword())) {
            return OperationResult.failure("Please make sure the new passwords match.");
        }

        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(resetPasswordForm.getEmail().trim())
                && user.getPhoneNumber().equals(resetPasswordForm.getPhoneNumber().trim())) {
                user.setPasswordHash(PasswordUtil.hashPassword(resetPasswordForm.getNewPassword()));
                userRepository.saveAll(users);
                return OperationResult.success("Password updated successfully. You can now login.", user);
            }
        }
        return OperationResult.failure("No account matches the provided email and phone number.");
    }
}
