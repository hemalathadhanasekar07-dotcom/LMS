package com.project.lms.service;

import com.project.lms.dto.AddTopicDTO;
import com.project.lms.dto.TopicResponseDTO;
import com.project.lms.entity.Topic;
import com.project.lms.entity.User;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.exception.ResourceNotFoundException;
import com.project.lms.exception.UnauthorizedActionException;
import com.project.lms.exception.UserNotFoundException;
import com.project.lms.repository.ModuleRepository;
import com.project.lms.repository.TopicRepository;
import com.project.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    private User getCurrentUser(String email) {
        log.debug("Fetching current user: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Current user not found: {}", email);
                    return new UserNotFoundException("CURRENT_USER_NOT_FOUND");
                });
    }

    private void validateAdmin(User user) {
        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            log.warn("Unauthorized access attempt by user: {}", user.getEmail());
            throw new AccessDeniedException("ADMIN_ONLY");
        }
    }

    private Topic dtoToEntity(AddTopicDTO dto, Long moduleId) {
        log.debug("Converting AddTopicDTO to Topic entity for module: {}", moduleId);

        Topic topic = new Topic();
        topic.setModuleId(moduleId);
        topic.setName(dto.getName());
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        return topic;
    }

    private TopicResponseDTO entityToDto(Topic topic) {
        return TopicResponseDTO.builder()
                .id(topic.getId())
                .moduleId(topic.getModuleId())
                .name(topic.getName())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .build();
    }

    public TopicResponseDTO addTopic(Long moduleId, AddTopicDTO dto) {
        log.info("Topic creation attempt: '{}' for module {}", dto.getName(), moduleId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        User currentUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> {
                    log.error("Logged-in user not found: {}", userName);
                    return new ResourceNotFoundException("USER_NOT_FOUND");
                });

        log.debug("Topic creation requested by user: {} with role: {}", currentUser.getEmail(), currentUser.getRole().getName());

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
            log.warn("Unauthorized topic creation attempt by user: {}", currentUser.getEmail());
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        moduleRepository.findById(moduleId)
                .orElseThrow(() -> {
                    log.error("Module not found while adding topic: {}", moduleId);
                    return new ResourceNotFoundException("MODULE_NOT_FOUND");
                });

        if (topicRepository.existsByModuleIdAndName(moduleId, dto.getName())) {
            log.warn("Duplicate topic '{}' in module {}", dto.getName(), moduleId);
            throw new DuplicateResourceException("TOPIC_ALREADY_EXISTS");
        }

        Topic saved = topicRepository.save(dtoToEntity(dto, moduleId));
        log.info("Topic created successfully with ID: {}", saved.getId());

        return entityToDto(saved);
    }

    public List<TopicResponseDTO> getTopics(Long moduleId) {
        log.info("Fetching topics for module ID: {}", moduleId);

        moduleRepository.findById(moduleId)
                .orElseThrow(() -> {
                    log.error("Module not found while fetching topics: {}", moduleId);
                    return new ResourceNotFoundException("MODULE_NOT_FOUND");
                });

        List<TopicResponseDTO> topics = topicRepository.findByModuleId(moduleId)
                .stream()
                .map(this::entityToDto)
                .toList();

        log.info("Total topics fetched for module {}: {}", moduleId, topics.size());
        return topics;
    }

    public Map<String, String> deleteTopic(Long id) {
        log.info("Topic deletion attempt for ID: {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        User currentUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> {
                    log.error("Logged-in user not found: {}", userName);
                    return new ResourceNotFoundException("USER_NOT_FOUND");
                });

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
            log.warn("Unauthorized topic deletion attempt by user: {}", currentUser.getEmail());
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Topic not found for deletion: {}", id);
                    return new ResourceNotFoundException("TOPIC_NOT_FOUND");
                });

        topicRepository.delete(topic);
        log.info("Topic deleted successfully: {}", id);

        return Map.of("message", "TOPIC_DELETED_SUCCESS");
    }
}