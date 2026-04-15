package com.cxcode.exam.application.port;

import com.cxcode.exam.application.AuditEvent;

public interface AuditLogger {
    void record(AuditEvent event);
}

