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

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("EMAIL_ALREADY_EXISTS");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("USER_ROLE_NOT_FOUND"));

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("ORG_NOT_FOUND"));

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
        mailService.sendUserRegistrationMail(user.getEmail(), user.getName());

        return Map.of(
                "message", "REGISTRATION_SUCCESS",
                "id", user.getId()
        );
    }



    public Map<String, Object> login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("INVALID_CREDENTIALS"));

        if (!user.getOrganization().getId().equals(request.getOrganizationId())) {
            throw new InvalidCredentialsException("INVALID_ORGANIZATION");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("INVALID_CREDENTIALS");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            throw new AccountStatusException("ACCOUNT_PENDING", "ACCOUNT_PENDING");
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            throw new AccountStatusException("ACCOUNT_REJECTED", "ACCOUNT_REJECTED");
        }

        String token = jwtService.generateToken(user);

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