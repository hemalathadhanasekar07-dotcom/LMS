package com.project.lms.service;

import com.project.lms.dto.AddUserRequestDTO;
import com.project.lms.dto.UserExportDTO;
import com.project.lms.dto.UserImportDTO;
import com.project.lms.dto.UserListResponseDTO;
import com.project.lms.entity.Organization;
import com.project.lms.entity.Role;
import com.project.lms.entity.User;
import com.project.lms.entity.UserStatus;
import com.project.lms.exception.DuplicateResourceException;
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

    public Map<String, Object> approveUser(Long id) {

        log.info("Approving user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setStatus(UserStatus.APPROVED);
        userRepository.save(user);

        log.info("User approved successfully: {}", id);

        return Map.of(
                "message", "User approved",
                "id", id
        );
    }
    public Map<String, Object> rejectUser(Long id) {

        log.info("Rejecting user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.REJECTED) {
            throw new IllegalStateException("User is already rejected");
        }

        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);

        log.info("User rejected successfully: {}", id);

        return Map.of(
                "message", "User rejected",
                "id", id
        );
    }
    public List<UserListResponseDTO> listUsers() {

        log.info("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(user -> UserListResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .email(user.getEmail())
                        .status(user.getStatus().name())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .toList();
    }
    public UserListResponseDTO getUserById(Long id, String currentUserEmail) {

        log.info("Fetching user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Get current logged-in user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        boolean isAdmin = currentUser.getRole().getName().equals("ADMIN");
        boolean isSameUser = currentUser.getId().equals(id);

        if (!isAdmin && !isSameUser) {

            throw new AccessDeniedException("Access denied");
        }

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
    public UserListResponseDTO addUser(AddUserRequestDTO request) {

        log.info("Admin creating user: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        Role role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Organization org = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .organization(org)
                .status(UserStatus.APPROVED)
                .build();

        userRepository.save(user);

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
    public List<UserExportDTO> exportUsers() {

        log.info("Exporting all users");

        List<User> users = userRepository.findAll();

        return users.stream()
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
    public Map<String, Object> importUsers(List<UserImportDTO> importList) {

        log.info("Importing {} users", importList.size());

        for (UserImportDTO dto : importList) {

            User user = userRepository.findById(dto.getId())
                    .orElseThrow(() ->
                            new UserNotFoundException("User not found"))

                    ;

            user.setUsername(dto.getUsername());
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());

            userRepository.save(user);
        }

        return Map.of("message", "Users imported successfully");
    }


}