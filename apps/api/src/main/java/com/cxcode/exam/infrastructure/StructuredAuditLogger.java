package com.cxcode.exam.infrastructure;

import com.cxcode.exam.application.AuditEvent;
import com.cxcode.exam.application.port.AuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StructuredAuditLogger implements AuditLogger {
    private static final Logger log = LoggerFactory.getLogger(StructuredAuditLogger.class);

    @Override
    public void record(AuditEvent event) {
        log.info(
                "event={} userId={} examId={} attemptId={} result={} errorCode={} occurredAt={}",
                event.event(),
                event.userId(),
                event.examId(),
                event.attemptId(),
                event.result(),
                event.errorCode(),
                event.occurredAt()
        );
    }
}

