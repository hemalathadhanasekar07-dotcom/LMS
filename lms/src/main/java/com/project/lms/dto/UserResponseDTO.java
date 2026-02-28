package com.project.lms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {


    private Long id;
    @Column(nullable = false)
    private String name;
    private String email;
    @NotBlank(message = "Role must not be empty")
    private String role;
    @NotBlank(message = "UserName must not be empty")
    private String userName;


//    @Size(min = 8, message = "Password must have at least 8 characters")
//    @Pattern(
//            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_-]).{8,}$",
//            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
//    )
//    private String password;

    @JsonProperty("organizationId")
    private Long organizationId;


}
