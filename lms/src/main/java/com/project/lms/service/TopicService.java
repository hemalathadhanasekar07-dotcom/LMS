package com.project.lms.service;

import com.project.lms.entity.Topic;
import com.project.lms.repository.ModuleRepository;
import com.project.lms.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;

    public Topic addTopic(Long moduleId, String name) {

        moduleRepository.findById(moduleId)
                .orElseThrow(() ->
                        new RuntimeException("Module not found"));

        if (topicRepository.existsByModuleIdAndName(moduleId, name)) {
            throw new IllegalArgumentException("Topic already exists for this module");
        }

        Topic topic = new Topic();
        topic.setModuleId(moduleId);
        topic.setName(name);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());

        return topicRepository.save(topic);
    }

    public List<Topic> getTopics(Long moduleId) {
        return topicRepository.findByModuleId(moduleId);
    }

    public Map<String, String> deleteTopic(Long id) {
        topicRepository.deleteById(id);
        return Map.of("message", "Topic deleted");
    }
}
