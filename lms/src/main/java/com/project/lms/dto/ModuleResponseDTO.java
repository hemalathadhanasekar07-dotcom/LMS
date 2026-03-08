package com.project.lms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ModuleResponseDTO {

    private Long id;
    private Long courseId;
    private String name;
    private Integer module_order;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}