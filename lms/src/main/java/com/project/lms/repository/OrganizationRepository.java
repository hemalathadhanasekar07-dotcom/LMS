package com.project.lms.repository;

import com.project.lms.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByCode(String code);

    boolean existsByCode(String code);
}