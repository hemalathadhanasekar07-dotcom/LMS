package com.project.lms.controller;

import com.project.lms.dto.AddCourseDTO;
import com.project.lms.dto.AddModuleDTO;
import com.project.lms.dto.AddTopicDTO;
import com.project.lms.service.CourseService;
import com.project.lms.service.ModuleService;
import com.project.lms.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final ModuleService moduleService;
    private final TopicService topicService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<?> addCourse(@Valid @RequestBody AddCourseDTO dto) {
        String user = getCurrentUserEmail();
        log.info("API CALL → Add course | User: {} | Code: {}", user, dto.getCode());

        return ResponseEntity.ok(courseService.addCourse(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        String user = getCurrentUserEmail();
        log.info("API CALL → Get all courses | User: {}", user);

        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        String user = getCurrentUserEmail();
        log.info("API CALL → Get course by ID | User: {} | Course ID: {}", user, id);

        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long id) {
        String user = getCurrentUserEmail();
        log.info("API CALL → Publish course | User: {} | Course ID: {}", user, id);

        return ResponseEntity.ok(courseService.publishCourse(id));
    }

    @PostMapping("/{courseId}/modules")
    public ResponseEntity<?> addModule(
            @PathVariable Long courseId,
            @RequestBody AddModuleDTO dto) {

        String user = getCurrentUserEmail();
        log.info("API CALL → Add module | User: {} | Course ID: {} | Module: {}",
                user, courseId, dto.getName());

        dto.setCourseId(courseId);

        return ResponseEntity.ok(moduleService.addModule(dto));
    }

    @GetMapping("/{courseId}/modules")
    public ResponseEntity<?> getModules(@PathVariable Long courseId) {
        String user = getCurrentUserEmail();
        log.info("API CALL → Get modules | User: {} | Course ID: {}", user, courseId);

        return ResponseEntity.ok(moduleService.getModules(courseId));
    }

    @PostMapping("/{courseId}/modules/{moduleId}/topics")
    public ResponseEntity<?> addTopic(
            @PathVariable Long moduleId,
            @Valid @RequestBody AddTopicDTO dto) {

        String user = getCurrentUserEmail();
        log.info("API CALL → Add topic | User: {} | Module ID: {} | Topic: {}",
                user, moduleId, dto.getName());

        return ResponseEntity.ok(topicService.addTopic(moduleId, dto));
    }

    @GetMapping("/{courseId}/modules/{moduleId}/topics")
    public ResponseEntity<?> getTopics(@PathVariable Long moduleId) {
        String user = getCurrentUserEmail();
        log.info("API CALL → Get topics | User: {} | Module ID: {}", user, moduleId);

        return ResponseEntity.ok(topicService.getTopics(moduleId));
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}/topics/{topicId}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long topicId) {
        String user = getCurrentUserEmail();
        log.info("API CALL → Delete topic | User: {} | Topic ID: {}", user, topicId);

        return ResponseEntity.ok(topicService.deleteTopic(topicId));
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportCourses() {
        String user = getCurrentUserEmail();
        log.info("API CALL → Export courses | User: {}", user);

        return ResponseEntity.ok(courseService.exportCourses());
    }
}