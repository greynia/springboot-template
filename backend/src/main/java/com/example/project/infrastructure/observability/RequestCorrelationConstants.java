package com.example.project.infrastructure.observability;

public final class RequestCorrelationConstants {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_ATTRIBUTE = "requestId";
    public static final String MDC_KEY = "requestId";

    private RequestCorrelationConstants() {
    }
}
