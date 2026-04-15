package com.cxcode.exam.api;

import jakarta.servlet.http.HttpServletRequest;

public final class CandidateIdentity {
    private CandidateIdentity() {
    }

    public static String from(HttpServletRequest request) {
        String userId = request.getHeader("x-user-id");
        return userId == null || userId.isBlank() ? "candidate-1" : userId;
    }
}

