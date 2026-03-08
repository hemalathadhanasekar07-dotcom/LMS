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



    private Module dtoToEntity(AddModuleDTO dto,int order) {

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
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userName = auth.getName();
        User currentUser=userRepository.findByEmail(userName).orElseThrow(()->new ResourceNotFoundException("USER_NOT_FOUND"));
        System.out.println(currentUser.getRole().getName());
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())){
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }

        log.info("Adding module {} to course {}", dto.getName(), dto.getCourseId());

        courseRepository.findById(dto.getCourseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("COURSE_NOT_FOUND"));

        if (moduleRepository.existsByCourseIdAndName(dto.getCourseId(), dto.getName())) {
            throw new DuplicateResourceException("MODULE_ALREADY_EXISTS");
        }
        int order = moduleRepository.countByCourseId(dto.getCourseId());

        Module saved = moduleRepository.save(dtoToEntity(dto,order));

        return entityToDto(saved);
    }



    public List<ModuleResponseDTO> getModules(Long courseId) {

        log.info("Fetching modules for course {}", courseId);

        courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("COURSE_NOT_FOUND"));

        return moduleRepository.findByCourseId(courseId)
                .stream()
                .map(this::entityToDto)
                .toList();
    }
}