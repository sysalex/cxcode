package com.cxcode.exam.domain;

import java.util.List;

public record CandidatePaper(String id, String title, List<CandidateQuestion> questions) {
}

