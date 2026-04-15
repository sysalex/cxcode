package com.cxcode.exam.api;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public final class RequestIds {
    private RequestIds() {
    }

    public static String from(HttpServletRequest request) {
        String requestId = request.getHeader("x-request-id");
        return requestId == null || requestId.isBlank() ? UUID.randomUUID().toString() : requestId;
    }
}

