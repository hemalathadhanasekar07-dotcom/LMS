package com.project.lms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    private Boolean active = true;

    private Long createdBy;
    private Long updatedBy;
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PRIVATE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}