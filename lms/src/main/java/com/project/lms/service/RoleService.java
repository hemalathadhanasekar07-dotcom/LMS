package com.project.lms.service;

import com.project.lms.dto.RoleRequestDTO;
import com.project.lms.dto.RoleResponseDTO;
import com.project.lms.entity.Role;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.repository.RoleRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Builder
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleResponseDTO createRole(RoleRequestDTO request) {

        log.info("Creating new role: {}", request.getName());

        if (roleRepository.findByName(request.getName()).isPresent()) {
            log.warn("Role already exists: {}", request.getName());
            throw new DuplicateResourceException("Role already exists");
        }

        Role role = Role.builder()
                .name(request.getName().toUpperCase())
                .build();

        roleRepository.save(role);

        log.info("Role created successfully with id: {}", role.getId());

        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
    public List<RoleResponseDTO> getAllRoles() {

        log.info("Fetching all roles");

        return roleRepository.findAll()
                .stream()
                .map(role -> RoleResponseDTO.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .createdAt(role.getCreatedAt())
                        .updatedAt(role.getUpdatedAt())
                        .build())
                .toList();
    }
}
