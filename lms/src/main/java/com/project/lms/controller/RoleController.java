package com.project.lms.controller;

import com.project.lms.dto.RoleDTO;
import com.project.lms.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Service
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> listRoles() {

        log.info("GET /api/roles called");

        List<RoleDTO> roles = roleService.getAllRoles();

        return ResponseEntity.ok(roles);
    }
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(
            @Valid @RequestBody RoleDTO request) {

        log.info("POST /api/roles called");

        RoleDTO response = roleService.createRole(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
