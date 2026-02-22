package com.project.lms.service;
import com.project.lms.entity.Module;
import com.project.lms.repository.CourseRepository;
import com.project.lms.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public Module addModule(Long courseId, String name) {

        courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        if (moduleRepository.existsByCourseIdAndName(courseId, name)) {
            throw new IllegalArgumentException("Module already exists for this course");
        }

        Module module = new Module();
        module.setCourseId(courseId);
        module.setName(name);
        module.setCreatedAt(LocalDateTime.now());
        module.setUpdatedAt(LocalDateTime.now());

        return moduleRepository.save(module);
    }

    public List<Module> getModules(Long courseId) {
        return moduleRepository.findByCourseId(courseId);
    }
}