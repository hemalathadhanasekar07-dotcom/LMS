package com.project.lms.service;

import com.project.lms.dto.OrganizationDTO;
import com.project.lms.entity.Organization;
import com.project.lms.entity.User;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.exception.ResourceNotFoundException;
import com.project.lms.exception.UnauthorizedActionException;
import com.project.lms.repository.OrganizationRepository;
import com.project.lms.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    private Organization dtoToEntity(OrganizationDTO dto){
        log.debug("Converting OrganizationDTO to Organization entity");
        Organization entity = new Organization();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        return entity;
    }

    private OrganizationDTO entityTODto(Organization entity){
        log.debug("Converting Organization entity to OrganizationDTO");
        OrganizationDTO dto=new OrganizationDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        return dto;
    }

    public User getCurrentUser(){
        log.debug("Fetching current logged-in user");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        log.debug("Current user email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(()-> {
                    log.error("Logged-in user not found with email: {}", email);
                    return new ResourceNotFoundException("Logged-in user not found");
                });
    }

    public List<OrganizationDTO> getAllOrganizations() {
        log.info("Fetching all organizations");
        List<OrganizationDTO> list = organizationRepository.findAll()
                .stream()
                .map(this::entityTODto)
                .toList();
        log.info("Total organizations fetched: {}", list.size());
        return list;
    }

    public OrganizationDTO createOrganization(OrganizationDTO organization) throws UnauthorizedActionException {
        log.info("Attempting to create organization with code: {}", organization.getCode());

        User currentUser = getCurrentUser();

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
            log.warn("Unauthorized organization creation attempt by user: {}", currentUser.getEmail());
            throw new UnauthorizedActionException("ORG_ADMIN_ONLY");
        }

        if (organizationRepository.existsByCode(organization.getCode())) {
            log.warn("Organization creation failed — code already exists: {}", organization.getCode());
            throw new DuplicateResourceException("ORG_CODE_EXISTS");
        }

        if (organizationRepository.existsByName(organization.getName())) {
            log.warn("Organization creation failed — name already exists: {}", organization.getName());
            throw new DuplicateResourceException("ORG_NAME_EXISTS");
        }

        Organization saved= organizationRepository.save(dtoToEntity(organization));
        log.info("Organization created successfully with ID: {}", saved.getId());

        return entityTODto(saved);
    }
}