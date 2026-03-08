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
        System.out.println("DTO STATUS = " + dto.getStatus());
        Authentication auth =SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userName = auth.getName();
        User currentUser=userRepository.findByEmail(userName).orElseThrow(()->new ResourceNotFoundException("USER_NOT_FOUND"));
        System.out.println(currentUser.getRole().getName());
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())){
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        if (dto.getCode() == null ||
                dto.getTitle() == null ||
                dto.getStatus() == null ||
                dto.getCreated_by() == null) {

            return Map.of(
                    "message",
                    "COURSE_REQUIRED_FIELDS"
            );
        }

        if (courseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("COURSE_CODE_EXISTS");
        }


        Course saved = courseRepository.save(dtoToEntity(dto));

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

        return courseRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }



    public CourseExportDTO getCourseById(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("COURSE_NOT_FOUND"));

        return entityToDto(course);
    }



    public Map<String, Object> exportCourses() {
        Authentication auth =SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userName = auth.getName();
        User currentUser=userRepository.findByEmail(userName).orElseThrow(()->new ResourceNotFoundException("USER_NOT_FOUND"));

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())){
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        List<CourseExportDTO> courses = courseRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();

        return Map.of(
                "message", "COURSES_EXPORTED_SUCCESS",
                "data", courses
        );
    }
    public Map<String, String> publishCourse(Long id) {
        //any one can do,,,later change it

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("COURSE_NOT_FOUND"));

        if (course.getStatus() == CourseStatus.PUBLISHED) {
            throw new IllegalStateException("COURSE_ALREADY_PUBLISHED");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);

        User creator = userRepository.findById(course.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));

        mailService.sendCoursePublishedMail(creator.getEmail(), course.getTitle());

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