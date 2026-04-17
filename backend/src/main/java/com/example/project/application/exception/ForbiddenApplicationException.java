package com.example.project.application.exception;

public class ForbiddenApplicationException extends ApplicationException {

    public ForbiddenApplicationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
