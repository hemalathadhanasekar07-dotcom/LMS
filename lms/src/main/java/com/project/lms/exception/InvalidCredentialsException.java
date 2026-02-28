package com.project.lms.exception;

public class InvalidCredentialsException extends RuntimeException {

    private final String messageKey;

    public InvalidCredentialsException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}