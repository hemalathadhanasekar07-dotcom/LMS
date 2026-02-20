package com.project.lms.service;

import com.project.lms.entity.Organization;
import com.project.lms.exception.OrganizationAlreadyExistsException;
import com.project.lms.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public Organization createOrganization(String code, String name) {

        log.info("Attempting to create organization with code: {}", code);


        if (organizationRepository.existsByCode(code)) {
            log.warn("Organization already exists with code: {}", code);
            throw new OrganizationAlreadyExistsException(code);
        }

        Organization organization = Organization.builder()
                .code(code)
                .name(name)
                .build();

        try {
            Organization saved = organizationRepository.save(organization);
            log.info("Organization created successfully with id: {}", saved.getId());
            return saved;
        } catch (DataIntegrityViolationException ex) {

            log.error("Duplicate entry detected at DB level for code: {}", code);
            throw new OrganizationAlreadyExistsException(code);
        }
    }

    public List<Organization> getAllOrganizations() {
        log.info("Fetching all organizations");
        return organizationRepository.findAll();
    }
}