package com.project.lms.service;

import com.project.lms.dto.*;
import com.project.lms.entity.Organization;
import com.project.lms.entity.Role;
import com.project.lms.entity.User;
import com.project.lms.entity.UserStatus;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.exception.ResourceNotFoundException;
import com.project.lms.exception.UserNotFoundException;
import com.project.lms.repository.OrganizationRepository;
import com.project.lms.repository.RoleRepository;
import com.project.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private User getCurrentUser(String email) {
        log.debug("Fetching current user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Current user not found: {}", email);
                    return new UserNotFoundException("CURRENT_USER_NOT_FOUND");
                });
    }

    private void validateAdmin(User user) {
        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("ADMIN_ONLY");
        }
    }

    private User dtoToEntity(AddUserRequestDTO dto, Role role, Organization org) {
        log.debug("Converting AddUserRequestDTO to User entity for email: {}", dto.getEmail());
        return User.builder()
                .username(dto.getUsername())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .organization(org)
                .status(UserStatus.APPROVED)
                .build();
    }

    private UserListResponseDTO entityToDto(User user) {
        return UserListResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserListResponseDTO addUser(AddUserRequestDTO request, String currentUserEmail) {
        log.info("Admin {} attempting to add user: {}", currentUserEmail, request.getEmail());

        User currentUser = getCurrentUser(currentUserEmail);
        validateAdmin(currentUser);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Add user failed — email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("EMAIL_ALREADY_EXISTS");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Add user failed — username already exists: {}", request.getUsername());
            throw new DuplicateResourceException("USERNAME_ALREADY_EXISTS");
        }

        Role role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> {
                    log.error("Role not found: {}", request.getRole());
                    return new ResourceNotFoundException("ROLE_NOT_FOUND");
                });

        Organization org = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> {
                    log.error("Organization not found: {}", request.getOrganizationId());
                    return new ResourceNotFoundException("ORG_NOT_FOUND");
                });

        User saved = userRepository.save(dtoToEntity(request, role, org));
        log.info("User created successfully with ID: {}", saved.getId());

        return entityToDto(saved);
    }

    public Map<String, Object> approveUser(Long id, String currentUserEmail) {
        log.info("Admin {} approving user ID: {}", currentUserEmail, id);

        User currentUser = getCurrentUser(currentUserEmail);
        validateAdmin(currentUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found for approval: {}", id);
                    return new UserNotFoundException("USER_NOT_FOUND");
                });

        if (user.getStatus() == UserStatus.APPROVED) {
            log.warn("User already approved: {}", id);
            throw new IllegalStateException("USER_ALREADY_APPROVED");
        }

        user.setStatus(UserStatus.APPROVED);
        userRepository.save(user);

        mailService.sendUserApprovedMail(user.getEmail(), user.getName());

        log.info("User approved successfully: {}", id);
        return Map.of("message", "USER_APPROVED_SUCCESS", "id", id);
    }

    public Map<String, Object> rejectUser(Long id, String currentUserEmail) {
        log.info("Admin {} rejecting user ID: {}", currentUserEmail, id);

        User currentUser = getCurrentUser(currentUserEmail);
        validateAdmin(currentUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found for rejection: {}", id);
                    return new UserNotFoundException("USER_NOT_FOUND");
                });

        if (user.getStatus() == UserStatus.REJECTED) {
            log.warn("User already rejected: {}", id);
            throw new IllegalStateException("USER_ALREADY_REJECTED");
        }

//        if (user.getStatus() == UserStatus.APPROVED) {
//            log.warn("Attempt to reject approved user: {}", id);
//            throw new IllegalStateException("APPROVED_USER_CANNOT_REJECT");
//        }

        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);
        mailService.sendUserRejectedMail(user.getEmail(), user.getName());

        log.info("User rejected successfully: {}", id);
        return Map.of("message", "USER_REJECTED_SUCCESS", "id", id);
    }

    public List<UserListResponseDTO> listUsers(String currentUserEmail) {
        log.info("Admin {} requesting user list", currentUserEmail);

        User currentUser = getCurrentUser(currentUserEmail);
        validateAdmin(currentUser);

        List<UserListResponseDTO> users = userRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();

        log.info("Total users fetched: {}", users.size());
        return users;
    }

    public UserListResponseDTO getUserById(Long id, String currentUserEmail) {
        log.debug("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found: {}", id);
                    return new UserNotFoundException("USER_NOT_FOUND");
                });

        User currentUser = getCurrentUser(currentUserEmail);

        boolean isAdmin = currentUser.getRole().getName().equalsIgnoreCase("ADMIN");
        boolean isSameUser = currentUser.getId().equals(id);

        if (!isAdmin && !isSameUser) {
            log.warn("Access denied for user {} to view user {}", currentUserEmail, id);
            throw new AccessDeniedException("ACCESS_DENIED");
        }

        return entityToDto(user);
    }

    public List<UserExportDTO> exportUsers(String currentUserEmail) {
        log.info("Admin {} exporting users", currentUserEmail);

        User currentUser = getCurrentUser(currentUserEmail);
        validateAdmin(currentUser);

        return userRepository.findAll()
                .stream()
                .map(user -> UserExportDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .email(user.getEmail())
                        .organizationId(user.getOrganization().getId())
                        .role(user.getRole().getName())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .toList();
    }

    public Map<String, Object> importUsers(List<UserImportDTO> importList, String currentUserEmail) {
        log.info("Admin {} importing {} users", currentUserEmail, importList.size());

        User currentUser = getCurrentUser(currentUserEmail);
        validateAdmin(currentUser);

        for (UserImportDTO dto : importList) {
            log.debug("Importing user ID: {}", dto.getId());

            User user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> {
                        log.error("User not found during import: {}", dto.getId());
                        return new UserNotFoundException("USER_NOT_FOUND");
                    });

            user.setUsername(dto.getUsername());
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());

            userRepository.save(user);
        }

        log.info("Users imported successfully");
        return Map.of("message", "USERS_IMPORTED_SUCCESS");
    }
}