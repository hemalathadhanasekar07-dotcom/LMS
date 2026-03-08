package com.project.lms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TopicResponseDTO {

    private Long id;
    private Long moduleId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}