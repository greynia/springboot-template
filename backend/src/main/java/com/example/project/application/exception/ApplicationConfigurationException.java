package com.example.project.application.exception;

public class ApplicationConfigurationException extends ApplicationException {

    public ApplicationConfigurationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
