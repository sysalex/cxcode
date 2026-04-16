CREATE TABLE exams (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    status VARCHAR(32) NOT NULL,
    start_at TIMESTAMP(6) NOT NULL,
    end_at TIMESTAMP(6) NOT NULL,
    duration_minutes INT NOT NULL,
    paper_id VARCHAR(64) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 1,
    INDEX idx_exams_start_at (start_at)
);

CREATE TABLE exam_candidates (
    id VARCHAR(140) PRIMARY KEY,
    exam_id VARCHAR(64) NOT NULL,
    candidate_id VARCHAR(64) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_exam_candidates_exam_candidate (exam_id, candidate_id),
    INDEX idx_exam_candidates_candidate (candidate_id),
    CONSTRAINT fk_exam_candidates_exam FOREIGN KEY (exam_id) REFERENCES exams (id)
);

CREATE TABLE papers (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    assigned_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 1
);

CREATE TABLE paper_questions (
    id VARCHAR(140) PRIMARY KEY,
    paper_id VARCHAR(64) NOT NULL,
    question_version_id VARCHAR(64) NOT NULL,
    question_order INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_paper_questions_paper_question (paper_id, question_version_id),
    INDEX idx_paper_questions_paper_order (paper_id, question_order),
    CONSTRAINT fk_paper_questions_paper FOREIGN KEY (paper_id) REFERENCES papers (id)
);

CREATE TABLE questions (
    version_id VARCHAR(64) PRIMARY KEY,
    question_id VARCHAR(64) NOT NULL,
    type VARCHAR(32) NOT NULL,
    prompt TEXT NOT NULL,
    points INT NOT NULL,
    max_length INT NULL,
    correct_option_id VARCHAR(64) NULL,
    reference_answer TEXT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 1,
    INDEX idx_questions_question_id (question_id)
);

CREATE TABLE question_options (
    id VARCHAR(140) PRIMARY KEY,
    question_version_id VARCHAR(64) NOT NULL,
    option_id VARCHAR(64) NOT NULL,
    option_label VARCHAR(500) NOT NULL,
    option_order INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_question_options_question_option (question_version_id, option_id),
    INDEX idx_question_options_question_order (question_version_id, option_order),
    CONSTRAINT fk_question_options_question FOREIGN KEY (question_version_id) REFERENCES questions (version_id)
);

CREATE TABLE attempts (
    id VARCHAR(64) PRIMARY KEY,
    exam_id VARCHAR(64) NOT NULL,
    candidate_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    started_at TIMESTAMP(6) NOT NULL,
    ends_at TIMESTAMP(6) NOT NULL,
    submitted_at TIMESTAMP(6) NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_attempts_exam_candidate (exam_id, candidate_id),
    INDEX idx_attempts_candidate (candidate_id),
    CONSTRAINT fk_attempts_exam FOREIGN KEY (exam_id) REFERENCES exams (id)
);

CREATE TABLE answer_sheets (
    id VARCHAR(64) PRIMARY KEY,
    attempt_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    revision BIGINT NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    submitted_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 1,
    UNIQUE KEY uk_answer_sheets_attempt (attempt_id),
    CONSTRAINT fk_answer_sheets_attempt FOREIGN KEY (attempt_id) REFERENCES attempts (id)
);

CREATE TABLE answer_items (
    id VARCHAR(140) PRIMARY KEY,
    answer_sheet_id VARCHAR(64) NOT NULL,
    question_id VARCHAR(64) NOT NULL,
    type VARCHAR(32) NOT NULL,
    selected_option_id VARCHAR(64) NULL,
    text_answer TEXT NULL,
    answer_order INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_answer_items_sheet_question (answer_sheet_id, question_id),
    INDEX idx_answer_items_sheet_order (answer_sheet_id, answer_order),
    CONSTRAINT fk_answer_items_sheet FOREIGN KEY (answer_sheet_id) REFERENCES answer_sheets (id)
);

CREATE TABLE submission_idempotency_records (
    idempotency_key VARCHAR(200) PRIMARY KEY,
    attempt_id VARCHAR(64) NOT NULL,
    event_name VARCHAR(64) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_submission_idempotency_attempt FOREIGN KEY (attempt_id) REFERENCES attempts (id)
);
