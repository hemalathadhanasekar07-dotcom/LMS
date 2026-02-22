package com.project.lms.repository;

import com.project.lms.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByModuleId(Long moduleId);
    boolean existsByModuleIdAndName(Long moduleId, String name);
}
