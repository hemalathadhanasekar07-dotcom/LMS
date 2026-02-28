package com.project.lms.controller;

import com.project.lms.dto.OrganizationDTO;
import com.project.lms.exception.UnauthorizedActionException;
import com.project.lms.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizations")
@Slf4j
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public List<OrganizationDTO> getAllOrganizations() {
        log.info("GET /api/organizations called");

        List<OrganizationDTO> organizations = organizationService.getAllOrganizations();

        log.info("Found {} organizations", organizations.size());
        return organizations;
    }

    @PostMapping
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationDTO dto) throws UnauthorizedActionException {
        log.info("POST /api/organizations called with payload: {}", dto);

        OrganizationDTO saved = organizationService.createOrganization(dto);

        log.info("Organization created with id={}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message","Organization created successfully",
                        "organizationid",saved.getId()
                )
        );
    }
}