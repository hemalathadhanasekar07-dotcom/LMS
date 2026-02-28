package com.project.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDTO {

    @NotBlank(message = "Organization code is required")
    private String code;

    @NotBlank(message = "Organization name is required")
    private String name;
    private Long id;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
}