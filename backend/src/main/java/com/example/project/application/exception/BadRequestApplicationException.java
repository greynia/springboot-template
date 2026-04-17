package com.example.project.application.exception;

public class BadRequestApplicationException extends ApplicationException {

    public BadRequestApplicationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
