package com.project.lms.exception;

public class UnauthorizedActionException extends RuntimeException {

    private final String messageKey;

    public UnauthorizedActionException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}