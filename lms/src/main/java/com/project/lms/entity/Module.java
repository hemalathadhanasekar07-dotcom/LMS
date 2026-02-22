package com.project.lms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "modules",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"course_id", "name"}
        )
)
@Getter
@Setter
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long courseId;

    private String name;

    private Integer moduleOrder = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}