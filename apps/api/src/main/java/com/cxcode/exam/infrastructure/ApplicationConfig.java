package com.cxcode.exam.infrastructure;

import com.cxcode.exam.application.ExamApplicationService;
import com.cxcode.exam.application.port.AuditLogger;
import com.cxcode.exam.application.port.ExamStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ApplicationConfig {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    SeedData seedData(Clock clock) {
        return SeedDataFactory.create(clock);
    }

    @Bean
    ExamApplicationService examApplicationService(
            ExamStore store,
            Clock clock,
            AuditLogger auditLogger
    ) {
        return new ExamApplicationService(store, clock, auditLogger);
    }
}

