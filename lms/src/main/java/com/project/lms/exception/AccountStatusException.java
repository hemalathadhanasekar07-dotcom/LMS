package com.project.lms.exception;

public class AccountStatusException extends RuntimeException {

    private final String messageKey;
    private final String code;

    public AccountStatusException(String messageKey, String code) {
        super(messageKey);
        this.messageKey = messageKey;
        this.code = code;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getCode() {
        return code;
    }
}