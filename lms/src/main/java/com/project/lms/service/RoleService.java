package com.project.lms.service;

import com.project.lms.dto.RoleDTO;
import com.project.lms.entity.Role;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;


    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private Role dtoToEntity(RoleDTO dto) {
        log.debug("Converting RoleDTO to Role entity");
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        return role;
    }

    private RoleDTO entityToDto(Role role) {
        log.debug("Converting Role entity to RoleDTO");
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }

    public RoleDTO createRole(RoleDTO request) {
        log.info("Attempting to create role with name: {}", request.getName());

        if (roleRepository.findByName(request.getName()).isPresent()) {
            log.warn("Role creation failed — role already exists: {}", request.getName());
            throw new DuplicateResourceException("ROLE_ALREADY_EXISTS");
        }

        Role role = dtoToEntity(request);
        role.setName(role.getName().toUpperCase());

        Role saved = roleRepository.save(role);
        log.info("Role created successfully with ID: {}", saved.getId());

        return entityToDto(saved);
    }

    public List<RoleDTO> getAllRoles() {
        log.info("Fetching all roles");

        List<RoleDTO> roles = roleRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();

        log.info("Total roles fetched: {}", roles.size());
        return roles;
    }
}