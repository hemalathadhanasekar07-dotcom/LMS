package com.project.lms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseExportDTO {

    private Long id;
    private String code;
    private String title;
    private String status;
    private Boolean active;

    private LocalDateTime created_at;
    private Long created_by;

    private LocalDateTime updated_at;
    private Long updated_by;

    private Long organization_id;
    private String visibility;
}