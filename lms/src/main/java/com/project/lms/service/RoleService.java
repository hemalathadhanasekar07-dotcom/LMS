package com.project.lms.service;

import com.project.lms.dto.RoleDTO;
import com.project.lms.entity.Role;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    // Constructor injection (same style as OrganizationService)
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }



    private Role dtoToEntity(RoleDTO dto) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        return role;
    }

    private RoleDTO entityToDto(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }



    public RoleDTO createRole(RoleDTO request) {

        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("ROLE_ALREADY_EXISTS");
        }

        Role role = dtoToEntity(request);
        role.setName(role.getName().toUpperCase());

        Role saved = roleRepository.save(role);

        return entityToDto(saved);
    }



    public List<RoleDTO> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }
}