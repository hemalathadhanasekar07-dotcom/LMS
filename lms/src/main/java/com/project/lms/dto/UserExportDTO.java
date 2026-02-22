package com.project.lms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserExportDTO {

    private Long id;
    private String username;
    private String name;
    private String email;
    private Long organizationId;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}