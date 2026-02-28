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

    // =========================================================
    // GET CURRENT USER EMAIL
    // =========================================================
    private String getCurrentUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    // =========================================================
    // APPROVE USER
    // =========================================================
    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.approveUser(id, getCurrentUserEmail())
        );
    }

    // =========================================================
    // REJECT USER
    // =========================================================
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.rejectUser(id, getCurrentUserEmail())
        );
    }

    // =========================================================
    // LIST USERS (ADMIN ONLY)
    // =========================================================
    @GetMapping
    public ResponseEntity<List<UserListResponseDTO>> listUsers() {

        return ResponseEntity.ok(
                userService.listUsers(getCurrentUserEmail())
        );
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<UserListResponseDTO> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUserById(id, getCurrentUserEmail())
        );
    }

    // =========================================================
    // ADD USER (ADMIN ONLY)
    // =========================================================
    @PostMapping
    public ResponseEntity<UserListResponseDTO> addUser(
            @Valid @RequestBody AddUserRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addUser(request, getCurrentUserEmail()));
    }

    // =========================================================
    // EXPORT USERS (ADMIN ONLY)
    // =========================================================
    @GetMapping("/export")
    public ResponseEntity<List<UserExportDTO>> exportUsers() {

        return ResponseEntity.ok(
                userService.exportUsers(getCurrentUserEmail())
        );
    }

    // =========================================================
    // IMPORT USERS (ADMIN ONLY)
    // =========================================================
    @PostMapping("/import")
    public ResponseEntity<?> importUsers(
            @RequestBody List<UserImportDTO> importList) {

        return ResponseEntity.ok(
                userService.importUsers(importList, getCurrentUserEmail())
        );
    }
}