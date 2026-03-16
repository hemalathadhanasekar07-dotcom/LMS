package com.project.lms.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddTopicDTO {

    @NotBlank(message = "TOPIC_NAME_REQUIRED")
    @Size(min = 3, max = 100, message = "TOPIC_NAME_SIZE_INVALID")
    private String name;
}