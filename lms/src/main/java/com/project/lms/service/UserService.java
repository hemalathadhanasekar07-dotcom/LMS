package com.project.lms.service;

import com.project.lms.entity.User;
import com.project.lms.entity.UserStatus;
import com.project.lms.exception.UserNotFoundException;
import com.project.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public Map<String, Object> approveUser(Long id) {

        log.info("Approving user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setStatus(UserStatus.APPROVED);
        userRepository.save(user);

        log.info("User approved successfully: {}", id);

        return Map.of(
                "message", "User approved",
                "id", id
        );
    }
}