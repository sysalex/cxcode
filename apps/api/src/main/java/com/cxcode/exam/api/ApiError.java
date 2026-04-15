package com.cxcode.exam.api;

import java.util.Map;

public record ApiError(
        String code,
        String message,
        String requestId,
        Map<String, Object> details
) {
}

