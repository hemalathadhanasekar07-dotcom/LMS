package com.project.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCourseDTO {

    @NotBlank(message = "COURSE_CODE_REQUIRED")
    private String code;

    @NotBlank(message = "COURSE_TITLE_REQUIRED")
    private String title;

    @NotBlank(message = "COURSE_STATUS_REQUIRED")
    private String status;

    @NotNull(message = "COURSE_CREATED_BY_REQUIRED")
    private Long created_by;
}