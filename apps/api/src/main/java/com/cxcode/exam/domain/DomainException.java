package com.cxcode.exam.domain;

import java.util.Collections;
import java.util.Map;

public class DomainException extends RuntimeException {
    private final String code;
    private final Map<String, Object> details;

    public DomainException(String code, String message) {
        this(code, message, Map.of());
    }

    public DomainException(String code, String message, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.details = Collections.unmodifiableMap(details);
    }

    public String code() {
        return code;
    }

    public Map<String, Object> details() {
        return details;
    }
}

