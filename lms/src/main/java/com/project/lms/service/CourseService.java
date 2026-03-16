package com.project.lms.service;

import com.project.lms.dto.AddCourseDTO;
import com.project.lms.dto.CourseExportDTO;
import com.project.lms.entity.Course;
import com.project.lms.entity.CourseStatus;
import com.project.lms.entity.User;
import com.project.lms.entity.Visibility;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.exception.ResourceNotFoundException;
import com.project.lms.exception.UnauthorizedActionException;
import com.project.lms.repository.CourseRepository;
import com.project.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    private Course dtoToEntity(AddCourseDTO dto) {
        log.debug("Converting AddCourseDTO to Course entity with code: {}", dto.getCode());

        return Course.builder()
                .code(dto.getCode())
                .title(dto.getTitle())
                .status(dto.getStatus())
                .createdBy(dto.getCreated_by())
                .updatedBy(dto.getCreated_by())
                .organizationId(1L)
                .active(true)
                .visibility(Visibility.PRIVATE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Object addCourse(AddCourseDTO dto) {
        log.info("Course creation attempt for code: {}", dto.getCode());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        User currentUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> {
                    log.error("Logged-in user not found: {}", userName);
                    return new ResourceNotFoundException("USER_NOT_FOUND");
                });

        log.debug("Course creation requested by user: {} with role: {}", currentUser.getEmail(), currentUser.getRole().getName());

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
            log.warn("Unauthorized course creation attempt by user: {}", currentUser.getEmail());
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        if (dto.getCode() == null ||
                dto.getTitle() == null ||
                dto.getStatus() == null ||
                dto.getCreated_by() == null) {

            log.warn("Course creation failed — missing required fields");
            return Map.of("message", "COURSE_REQUIRED_FIELDS");
        }

        if (courseRepository.existsByCode(dto.getCode())) {
            log.warn("Course creation failed — duplicate course code: {}", dto.getCode());
            throw new DuplicateResourceException("COURSE_CODE_EXISTS");
        }

        Course saved = courseRepository.save(dtoToEntity(dto));
        log.info("Course created successfully with ID: {}", saved.getId());

        if (saved.getStatus() == CourseStatus.PUBLISHED) {

            List<User> students = userRepository.findAll();

            for (User student : students) {
                mailService.sendCoursePublishedMail(
                        student.getEmail(),
                        student.getName(),
                        saved.getTitle()
                );
            }

            System.out.println("Course publish emails sent");
        }

        return Map.of(
                "id", saved.getId(),
                "code", saved.getCode(),
                "title", saved.getTitle(),
                "status", saved.getStatus(),
                "active", saved.getActive(),
                "created_by", saved.getCreatedBy(),
                "updated_by", saved.getUpdatedBy(),
                "organization_id", saved.getOrganizationId(),
                "visibility", saved.getVisibility()
        );
    }

    public List<CourseExportDTO> getAllCourses() {
        log.info("Fetching all courses");

        List<CourseExportDTO> list = courseRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();

        log.info("Total courses fetched: {}", list.size());
        return list;
    }

    public CourseExportDTO getCourseById(Long id) {
        log.debug("Fetching course by ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found: {}", id);
                    return new ResourceNotFoundException("COURSE_NOT_FOUND");
                });

        return entityToDto(course);
    }

    public Map<String, Object> exportCourses() {
        log.info("Course export requested");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        User currentUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> {
                    log.error("Logged-in user not found: {}", userName);
                    return new ResourceNotFoundException("USER_NOT_FOUND");
                });

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
            log.warn("Unauthorized course export attempt by user: {}", currentUser.getEmail());
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        List<CourseExportDTO> courses = courseRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();

        log.info("Courses exported successfully. Count: {}", courses.size());

        return Map.of(
                "message", "COURSES_EXPORTED_SUCCESS",
                "data", courses
        );
    }

    public Map<String, String> publishCourse(Long id) {
        log.info("Publishing course ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found for publishing: {}", id);
                    return new ResourceNotFoundException("COURSE_NOT_FOUND");
                });

        if (course.getStatus() == CourseStatus.PUBLISHED) {
            log.warn("Course already published: {}", id);
            throw new IllegalStateException("COURSE_ALREADY_PUBLISHED");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);

        log.info("Course published successfully: {}", id);

        User creator = userRepository.findById(course.getCreatedBy())
                .orElseThrow(() -> {
                    log.error("Course creator not found: {}", course.getCreatedBy());
                    return new ResourceNotFoundException("USER_NOT_FOUND");
                });

        mailService.sendCoursePublishedMail(
                creator.getEmail(),
                creator.getName(),
                course.getTitle()
        );
        log.info("Course publication email sent to: {}", creator.getEmail());

        return Map.of("message", "COURSE_PUBLISHED_SUCCESS");
    }

    private CourseExportDTO entityToDto(Course course) {
        return CourseExportDTO.builder()
                .id(course.getId())
                .code(course.getCode())
                .title(course.getTitle())
                .status(course.getStatus() != null ? course.getStatus().name() : null)
                .active(course.getActive())
                .created_at(course.getCreatedAt())
                .created_by(course.getCreatedBy())
                .updated_at(course.getUpdatedAt())
                .updated_by(course.getUpdatedBy())
                .organization_id(course.getOrganizationId())
                .visibility(course.getVisibility() != null ? course.getVisibility().name() : null)
                .build();
    }
}