package com.project.lms.service;

import com.project.lms.dto.*;

import com.project.lms.entity.Organization;
import com.project.lms.entity.Role;
import com.project.lms.entity.User;
import com.project.lms.entity.UserStatus;
import com.project.lms.exception.AccountStatusException;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.exception.InvalidCredentialsException;
import com.project.lms.exception.ResourceNotFoundException;
import com.project.lms.repository.*;
import com.project.lms.security.JwtService;
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
    private final JwtService jwtService;
    private final MailService mailService;

    public Map<String, Object> register(RegisterDTO request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed — email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("EMAIL_ALREADY_EXISTS");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> {
                    log.error("Default USER role not found during registration");
                    return new ResourceNotFoundException("USER_ROLE_NOT_FOUND");
                });

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> {
                    log.error("Organization not found during registration: {}", request.getOrganizationId());
                    return new ResourceNotFoundException("ORG_NOT_FOUND");
                });

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .organization(organization)
                .status(UserStatus.PENDING)
                .build();

        userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        mailService.sendUserRegistrationMail(user.getEmail(), user.getName());
        log.info("Registration email sent to: {}", user.getEmail());

        return Map.of(
                "message", "REGISTRATION_SUCCESS",
                "id", user.getId()
        );
    }

    public Map<String, Object> login(LoginRequestDTO request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed — invalid email: {}", request.getEmail());
                    return new InvalidCredentialsException("INVALID_CREDENTIALS");
                });

        if (!user.getOrganization().getId().equals(request.getOrganizationId())) {
            log.warn("Login failed — invalid organization for user: {}", request.getEmail());
            throw new InvalidCredentialsException("INVALID_ORGANIZATION");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed — incorrect password for user: {}", request.getEmail());
            throw new InvalidCredentialsException("INVALID_CREDENTIALS");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            log.warn("Login blocked — account pending approval: {}", request.getEmail());
            throw new AccountStatusException("ACCOUNT_PENDING", "ACCOUNT_PENDING");
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            log.warn("Login blocked — account rejected: {}", request.getEmail());
            throw new AccountStatusException("ACCOUNT_REJECTED", "ACCOUNT_REJECTED");
        }

        String token = jwtService.generateToken(user);
        log.info("Login successful for user ID: {}", user.getId());

        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .organizationId(user.getOrganization().getId())
                .build();

        return Map.of(
                "token", token,
                "user", responseDTO
        );
    }
}