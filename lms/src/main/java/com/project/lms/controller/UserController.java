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

    private String getCurrentUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveUser(@PathVariable Long id) {
        String admin = getCurrentUserEmail();
        log.info("API CALL → Approve user | Admin: {} | Target User ID: {}", admin, id);

        return ResponseEntity.ok(
                userService.approveUser(id, admin)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectUser(@PathVariable Long id) {
        String admin = getCurrentUserEmail();
        log.info("API CALL → Reject user | Admin: {} | Target User ID: {}", admin, id);

        return ResponseEntity.ok(
                userService.rejectUser(id, admin)
        );
    }

    @GetMapping
    public ResponseEntity<List<UserListResponseDTO>> listUsers() {
        String admin = getCurrentUserEmail();
        log.info("API CALL → List users | Requested by: {}", admin);

        return ResponseEntity.ok(
                userService.listUsers(admin)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserListResponseDTO> getUserById(@PathVariable Long id) {
        String requester = getCurrentUserEmail();
        log.info("API CALL → Get user by ID | Requested by: {} | User ID: {}", requester, id);

        return ResponseEntity.ok(
                userService.getUserById(id, requester)
        );
    }

    @PostMapping
    public ResponseEntity<UserListResponseDTO> addUser(
            @Valid @RequestBody AddUserRequestDTO request) {

        String admin = getCurrentUserEmail();
        log.info("API CALL → Add user | Admin: {} | Email: {}", admin, request.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addUser(request, admin));
    }

    @GetMapping("/export")
    public ResponseEntity<List<UserExportDTO>> exportUsers() {
        String admin = getCurrentUserEmail();
        log.info("API CALL → Export users | Admin: {}", admin);

        return ResponseEntity.ok(
                userService.exportUsers(admin)
        );
    }

    @PostMapping("/import")
    public ResponseEntity<?> importUsers(
            @RequestBody List<UserImportDTO> importList) {

        String admin = getCurrentUserEmail();
        log.info("API CALL → Import users | Admin: {} | Records: {}", admin, importList.size());

        return ResponseEntity.ok(
                userService.importUsers(importList, admin)
        );
    }
}