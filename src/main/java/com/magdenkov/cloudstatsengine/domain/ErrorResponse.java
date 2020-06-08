package com.magdenkov.cloudstatsengine.domain;

public class ErrorResponse {
    private String message;
    private String exceptionType;

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
