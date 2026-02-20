package com.project.lms.controller;

import com.project.lms.dto.LoginRequestDTO;
import com.project.lms.dto.RegisterRequestDTO;
import com.project.lms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        log.info("POST /api/auth/register called");

        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        log.info("POST /api/auth/login called");

        return ResponseEntity.ok(authService.login(request));
    }
}