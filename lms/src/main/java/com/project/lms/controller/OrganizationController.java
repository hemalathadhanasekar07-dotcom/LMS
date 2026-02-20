package com.project.lms.controller;

import com.project.lms.entity.Organization;
import com.project.lms.repository.OrganizationRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@Slf4j
public class OrganizationController {

    private final OrganizationRepository organizationRepository;

    public OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @GetMapping
    public List<Organization> getAllOrganizations() {
        log.info("GET /api/organizations called");

        List<Organization> organizations = organizationRepository.findAll();

        log.info("Found {} organizations", organizations.size());
        return organizations;
    }

    @PostMapping
    public ResponseEntity<Organization> createOrganization(@Valid @RequestBody Organization organization) {
        log.info("POST /api/organizations called with payload: {}", organization);

        Organization saved = organizationRepository.save(organization);

        log.info("Organization created with id={}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
