package com.cxcode.exam.api;

import com.cxcode.exam.application.ApplicationException;
import com.cxcode.exam.domain.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(
            DomainException error,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        return ResponseEntity
                .status(statusFor(error.code()))
                .body(ApiResponse.error(new ApiError(
                        error.code(),
                        error.getMessage(),
                        requestId,
                        error.details()
                ), requestId));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplicationException(
            ApplicationException error,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        return ResponseEntity
                .status(statusFor(error.code()))
                .body(ApiResponse.error(new ApiError(
                        error.code(),
                        error.getMessage(),
                        requestId,
                        Map.of()
                ), requestId));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            Exception error,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        Map<String, Object> details = new HashMap<>();
        details.put("reason", error.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(new ApiError(
                        "VALIDATION_ERROR",
                        "请求参数无效。",
                        requestId,
                        details
                ), requestId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception error,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiError(
                        "UNEXPECTED_ERROR",
                        "服务暂时不可用。",
                        requestId,
                        Map.of()
                ), requestId));
    }

    private HttpStatus statusFor(String code) {
        return switch (code) {
            case "PERMISSION_DENIED" -> HttpStatus.FORBIDDEN;
            case "EXAM_NOT_FOUND", "PAPER_NOT_FOUND", "ATTEMPT_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "EXAM_NOT_OPEN",
                    "EXAM_NOT_STARTED",
                    "EXAM_ALREADY_CLOSED",
                    "ANSWER_SHEET_LOCKED",
                    "ANSWER_REVISION_CONFLICT" -> HttpStatus.CONFLICT;
            default -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }
}

