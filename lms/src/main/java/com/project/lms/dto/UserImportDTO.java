package com.project.lms.dto;

import lombok.Data;

@Data
public class UserImportDTO {

    private Long id;
    private String username;
    private String name;
    private String email;
    private Long organizationId;
    private String role;
}