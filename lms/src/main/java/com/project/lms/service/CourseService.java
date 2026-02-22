package com.project.lms.service;

import com.project.lms.dto.AddCourseDTO;
import com.project.lms.dto.CourseExportDTO;
import com.project.lms.entity.Course;
import com.project.lms.entity.CourseStatus;
import com.project.lms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Object addCourse(AddCourseDTO dto) {

        if (dto.getCode() == null ||
                dto.getTitle() == null ||
                dto.getStatus() == null ||
                dto.getCreated_by() == null) {

            return Map.of("message",
                    "code, title, status, and created_by are required");
        }

        if (courseRepository.existsByCode(dto.getCode())) {
            return Map.of("message", "Course code already exists");
        }

        Course course = new Course();
        course.setCode(dto.getCode());
        course.setTitle(dto.getTitle());
        course.setStatus(CourseStatus.valueOf(dto.getStatus()));
        course.setCreatedBy(dto.getCreated_by());
        course.setUpdatedBy(dto.getCreated_by());
        course.setOrganizationId(1L); // temporary
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));
    }
    public List<CourseExportDTO> exportCourses() {

        return courseRepository.findAll()
                .stream()
                .map(course -> CourseExportDTO.builder()
                        .id(course.getId())
                        .code(course.getCode())
                        .title(course.getTitle())
                        .status(course.getStatus().name())
                        .active(course.getActive())
                        .created_at(course.getCreatedAt())
                        .created_by(course.getCreatedBy())
                        .updated_at(course.getUpdatedAt())
                        .updated_by(course.getUpdatedBy())
                        .organization_id(course.getOrganizationId())
                        .visibility(course.getVisibility().name())
                        .build())
                .toList();
    }
}