
package com.project.lms.controller;

import com.project.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}