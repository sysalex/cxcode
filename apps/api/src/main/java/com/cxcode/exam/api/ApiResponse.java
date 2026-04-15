package com.cxcode.exam.api;

public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError error,
        String requestId
) {
    public static <T> ApiResponse<T> ok(T data, String requestId) {
        return new ApiResponse<>(true, data, null, requestId);
    }

    public static ApiResponse<Void> error(ApiError error, String requestId) {
        return new ApiResponse<>(false, null, error, requestId);
    }
}

