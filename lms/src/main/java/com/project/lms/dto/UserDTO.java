package com.project.lms.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class UserDTO {



        private Long id;
        @NotBlank(message = "UserName must not be empty")
        private String userName;
        @NotBlank(message = "Email must not be empty")
        @Email(message = "Invalid email format")
        private String email;
        @NotBlank(message = "Password must not be empty")
        @Size(min = 8, message = "Password must have at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_-]).{8,}$",
                message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
        )
        private String password;
        @NotBlank(message = "Role must not be empty")
        private String role;
        @NotBlank(message = "name must not be empty")
        private String name;
        private String status;
        @NotNull(message = "Organization ID must not be null")
        private Long organizationId;
        private Long createdBy;
        private Long modifiedBy;

    }

