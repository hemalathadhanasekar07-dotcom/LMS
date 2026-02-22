package com.project.lms.dto;

import lombok.Data;

@Data
public class AddCourseDTO {

    private String code;
    private String title;
    private String status;
    private Long created_by;
}