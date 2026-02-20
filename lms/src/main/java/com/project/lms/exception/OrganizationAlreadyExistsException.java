package com.project.lms.exception;

public class OrganizationAlreadyExistsException extends RuntimeException {
    public OrganizationAlreadyExistsException(String code) {
        super("Organization already exists with code: " + code);
    }
}
