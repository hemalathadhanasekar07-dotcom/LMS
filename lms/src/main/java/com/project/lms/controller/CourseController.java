package com.project.lms.controller;

import com.project.lms.dto.AddCourseDTO;
import com.project.lms.dto.AddModuleDTO;
import com.project.lms.dto.AddTopicDTO;
import com.project.lms.service.CourseService;
import com.project.lms.service.ModuleService;
import com.project.lms.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final ModuleService moduleService;
    private final TopicService topicService;



    @PostMapping
    public ResponseEntity<?> addCourse(@Valid @RequestBody AddCourseDTO dto) {
        return ResponseEntity.ok(courseService.addCourse(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAllCourses() {

        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.publishCourse(id));
    }
    @PostMapping("/{courseId}/modules")
    public ResponseEntity<?> addModule(
            @PathVariable Long courseId,
            @RequestBody AddModuleDTO dto) {

        dto.setCourseId(courseId);

        return ResponseEntity.ok(
                moduleService.addModule(dto)
        );
    }

    @GetMapping("/{courseId}/modules")
    public ResponseEntity<?> getModules(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                moduleService.getModules(courseId)
        );
    }
    @PostMapping("/{courseId}/modules/{moduleId}/topics")
    public ResponseEntity<?> addTopic(
            @PathVariable Long courseId,
            @PathVariable Long moduleId,
            @Valid @RequestBody AddTopicDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();


        return ResponseEntity.ok(
                topicService.addTopic(moduleId,dto)
        );
    }

    @GetMapping("/{courseId}/modules/{moduleId}/topics")
    public ResponseEntity<?> getTopics(
            @PathVariable Long moduleId) {

        return ResponseEntity.ok(
                topicService.getTopics(moduleId)
        );
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}/topics/{topicId}")
    public ResponseEntity<?> deleteTopic(
            @PathVariable Long topicId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return ResponseEntity.ok(
                topicService.deleteTopic(topicId)
        );
    }
    @GetMapping("/export")
    public ResponseEntity<?> exportCourses() {
        return ResponseEntity.ok(courseService.exportCourses());
    }
}