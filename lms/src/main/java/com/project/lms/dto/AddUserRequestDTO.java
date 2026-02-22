package com.project.lms.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddUserRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Long organizationId;

    @NotBlank
    private String role; // USER / ADMIN
}