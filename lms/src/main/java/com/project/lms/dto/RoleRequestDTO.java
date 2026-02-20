package com.project.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequestDTO {
    @NotBlank(message = "Role name is required")
    private String name;
}
