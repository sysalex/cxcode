package com.cxcode.exam.api;

import com.cxcode.exam.application.AttemptView;
import com.cxcode.exam.application.CandidateExamList;
import com.cxcode.exam.application.ExamApplicationService;
import com.cxcode.exam.domain.Answer;
import com.cxcode.exam.domain.SubmitKind;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ExamController {
    private final ExamApplicationService service;

    public ExamController(ExamApplicationService service) {
        this.service = service;
    }

    @GetMapping("/exams")
    public ApiResponse<CandidateExamList> listExams(HttpServletRequest request) {
        String requestId = RequestIds.from(request);
        String candidateId = CandidateIdentity.from(request);
        return ApiResponse.ok(service.listCandidateExams(candidateId), requestId);
    }

    @PostMapping("/exams/{examId}/attempts")
    public ApiResponse<AttemptView> startAttempt(
            @PathVariable String examId,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        String candidateId = CandidateIdentity.from(request);
        return ApiResponse.ok(service.startOrResumeAttempt(examId, candidateId), requestId);
    }

    @GetMapping("/attempts/{attemptId}")
    public ApiResponse<AttemptView> getAttempt(
            @PathVariable String attemptId,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        String candidateId = CandidateIdentity.from(request);
        return ApiResponse.ok(service.getAttempt(attemptId, candidateId), requestId);
    }

    @PostMapping("/attempts/{attemptId}/answers/auto-save")
    public ApiResponse<AttemptView> autoSave(
            @PathVariable String attemptId,
            @Valid @RequestBody AutoSaveRequest body,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        String candidateId = CandidateIdentity.from(request);
        List<Answer> answers = body.answers().stream().map(AnswerDto::toDomain).toList();
        return ApiResponse.ok(
                service.autoSave(attemptId, candidateId, body.clientRevision(), answers),
                requestId
        );
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ApiResponse<AttemptView> submit(
            @PathVariable String attemptId,
            @RequestBody(required = false) SubmitRequest body,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        String candidateId = CandidateIdentity.from(request);
        String idempotencyKey = body == null || body.idempotencyKey() == null || body.idempotencyKey().isBlank()
                ? UUID.randomUUID().toString()
                : body.idempotencyKey();
        return ApiResponse.ok(
                service.submit(attemptId, candidateId, idempotencyKey, SubmitKind.MANUAL),
                requestId
        );
    }

    @PostMapping("/attempts/{attemptId}/timeout-submit")
    public ApiResponse<AttemptView> timeoutSubmit(
            @PathVariable String attemptId,
            @RequestBody(required = false) SubmitRequest body,
            HttpServletRequest request
    ) {
        String requestId = RequestIds.from(request);
        String candidateId = CandidateIdentity.from(request);
        String idempotencyKey = body == null || body.idempotencyKey() == null || body.idempotencyKey().isBlank()
                ? UUID.randomUUID().toString()
                : body.idempotencyKey();
        return ApiResponse.ok(
                service.submit(attemptId, candidateId, idempotencyKey, SubmitKind.TIMEOUT),
                requestId
        );
    }
}

