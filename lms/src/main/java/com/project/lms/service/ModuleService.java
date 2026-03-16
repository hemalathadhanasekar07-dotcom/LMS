package com.project.lms.service;

import com.project.lms.dto.AddModuleDTO;
import com.project.lms.dto.ModuleResponseDTO;
import com.project.lms.entity.Module;
import com.project.lms.entity.User;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.exception.ResourceNotFoundException;
import com.project.lms.exception.UnauthorizedActionException;
import com.project.lms.repository.CourseRepository;
import com.project.lms.repository.ModuleRepository;
import com.project.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private Module dtoToEntity(AddModuleDTO dto, int order) {
        log.debug("Converting AddModuleDTO to Module entity for course: {}", dto.getCourseId());

        return Module.builder()
                .courseId(dto.getCourseId())
                .name(dto.getName())
                .moduleOrder(order)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private ModuleResponseDTO entityToDto(Module module) {
        return ModuleResponseDTO.builder()
                .id(module.getId())
                .courseId(module.getCourseId())
                .name(module.getName())
                .module_order(module.getModuleOrder())
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();
    }

    public ModuleResponseDTO addModule(AddModuleDTO dto) {
        log.info("Module creation attempt: {} for course {}", dto.getName(), dto.getCourseId());

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userName = auth.getName();

        User currentUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> {
                    log.error("Logged-in user not found: {}", userName);
                    return new ResourceNotFoundException("USER_NOT_FOUND");
                });

        log.debug("Module creation requested by user: {} with role: {}", currentUser.getEmail(), currentUser.getRole().getName());

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
            log.warn("Unauthorized module creation attempt by user: {}", currentUser.getEmail());
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> {
                    log.error("Course not found while adding module: {}", dto.getCourseId());
                    return new ResourceNotFoundException("COURSE_NOT_FOUND");
                });

        if (moduleRepository.existsByCourseIdAndName(dto.getCourseId(), dto.getName())) {
            log.warn("Duplicate module name '{}' for course {}", dto.getName(), dto.getCourseId());
            throw new DuplicateResourceException("MODULE_ALREADY_EXISTS");
        }

        int order = moduleRepository.countByCourseId(dto.getCourseId());
        log.debug("Assigned module order {} for course {}", order, dto.getCourseId());

        Module saved = moduleRepository.save(dtoToEntity(dto, order));
        log.info("Module created successfully with ID: {}", saved.getId());

        return entityToDto(saved);
    }

    public List<ModuleResponseDTO> getModules(Long courseId) {
        log.info("Fetching modules for course ID: {}", courseId);

        courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course not found while fetching modules: {}", courseId);
                    return new ResourceNotFoundException("COURSE_NOT_FOUND");
                });

        List<ModuleResponseDTO> modules = moduleRepository.findByCourseId(courseId)
                .stream()
                .map(this::entityToDto)
                .toList();

        log.info("Total modules fetched for course {}: {}", courseId, modules.size());
        return modules;
    }
}
