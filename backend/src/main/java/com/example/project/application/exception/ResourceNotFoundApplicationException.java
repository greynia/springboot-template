package com.example.project.application.exception;

public class ResourceNotFoundApplicationException extends ApplicationException {

    public ResourceNotFoundApplicationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
