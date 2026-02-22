
package com.project.lms.controller;

import com.project.lms.dto.AddUserRequestDTO;
import com.project.lms.dto.UserExportDTO;
import com.project.lms.dto.UserImportDTO;
import com.project.lms.dto.UserListResponseDTO;
import com.project.lms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveUser(@PathVariable Long id) {

        log.info("PUT /api/users/{}/approve called", id);

        return ResponseEntity.ok(userService.approveUser(id));
    }
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectUser(@PathVariable Long id) {

        log.info("PUT /api/users/{}/reject called", id);

        return ResponseEntity.ok(userService.rejectUser(id));
    }
    @GetMapping
    public ResponseEntity<List<UserListResponseDTO>> listUsers() {

        log.info("GET /api/users called");

        return ResponseEntity.ok(userService.listUsers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserListResponseDTO> getUserById(@PathVariable Long id) {

        String currentUserEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(userService.getUserById(id, currentUserEmail));
    }
    @PostMapping
    public ResponseEntity<UserListResponseDTO> addUser(
            @Valid @RequestBody AddUserRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addUser(request));
    }
    @GetMapping("/export")
    public ResponseEntity<List<UserExportDTO>> exportUsers() {

        List<UserExportDTO> users = userService.exportUsers();

        return ResponseEntity.ok(users);
    }
    @PostMapping("/import")
    public ResponseEntity<?> importUsers(
            @RequestBody List<UserImportDTO> importList) {

        return ResponseEntity.ok(userService.importUsers(importList));
    }

}