package com.project.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddModuleDTO {

    @NotNull(message = "COURSE_ID_REQUIRED")
    private Long courseId;

    @NotBlank(message = "MODULE_NAME_REQUIRED")
    private String name;
}