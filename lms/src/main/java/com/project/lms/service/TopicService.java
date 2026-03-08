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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;


    private User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("CURRENT_USER_NOT_FOUND"));
    }

    private void validateAdmin(User user) {
        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            throw new AccessDeniedException("ADMIN_ONLY");
        }
    }


    private Topic dtoToEntity(AddTopicDTO dto, Long moduleId) {
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

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userName = auth.getName();
        User currentUser=userRepository.findByEmail(userName).orElseThrow(()->new ResourceNotFoundException("USER_NOT_FOUND"));
        System.out.println(currentUser.getRole().getName());
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())){
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("MODULE_NOT_FOUND"));

        if (topicRepository.existsByModuleIdAndName(moduleId, dto.getName())) {
            throw new DuplicateResourceException("TOPIC_ALREADY_EXISTS");
        }

        Topic saved = topicRepository.save(dtoToEntity(dto, moduleId));

        return entityToDto(saved);
    }


    public List<TopicResponseDTO> getTopics(Long moduleId) {

        moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("MODULE_NOT_FOUND"));

        return topicRepository.findByModuleId(moduleId)
                .stream()
                .map(this::entityToDto)
                .toList();
    }


    public Map<String, String> deleteTopic(Long id) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userName = auth.getName();
        User currentUser=userRepository.findByEmail(userName).orElseThrow(()->new ResourceNotFoundException("USER_NOT_FOUND"));
        System.out.println(currentUser.getRole().getName());
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())){
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TOPIC_NOT_FOUND"));

        topicRepository.delete(topic);

        return Map.of("message", "TOPIC_DELETED_SUCCESS");
    }
}