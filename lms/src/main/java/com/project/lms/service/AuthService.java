package com.project.lms.service;

import com.project.lms.dto.LoginRequestDTO;
import com.project.lms.dto.RegisterRequestDTO;
import com.project.lms.entity.*;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> register(RegisterRequestDTO request) {

        log.info("Register request received for email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("Email already exists");
        }

        // Fetch USER role
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        // Fetch organization
        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Build user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .username(request.getEmail()) // username same as email
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .organization(organization)
                .status(UserStatus.PENDING)
                .build();

        userRepository.save(user);

        log.info("User registered successfully with id: {}", user.getId());

        return Map.of(
                "message", "Registration successful. Your account is pending approval by an administrator.",
                "id", user.getId()
        );
    }
    public Map<String, Object> login(LoginRequestDTO request) {

        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check organization match
        if (!user.getOrganization().getId().equals(request.getOrganizationId())) {
            throw new RuntimeException("Invalid organization");
        }

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for email: {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        // If user still pending
        if (user.getStatus() == UserStatus.PENDING) {
            return Map.of(
                    "message", "Your account is pending approval. Please contact the administrator.",
                    "code", "ACCOUNT_PENDING"
            );
        }

        log.info("Login successful for user id: {}", user.getId());

        return Map.of(
                "message", "Login successful",
                "id", user.getId(),
                "role", user.getRole().getName()
        );
    }
}