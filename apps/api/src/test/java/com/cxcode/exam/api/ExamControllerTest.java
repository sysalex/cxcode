package com.cxcode.exam.api;

import com.cxcode.exam.ExamApiApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ExamApiApplication.class)
@AutoConfigureMockMvc
class ExamControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listsCandidateExams() throws Exception {
        mockMvc.perform(get("/api/v1/exams").header("x-user-id", "candidate-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.exams[0].id", is("exam-demo-1")));
    }

    @Test
    void startsAttemptAndAutoSaves() throws Exception {
        String startBody = mockMvc.perform(post("/api/v1/exams/exam-demo-1/attempts")
                        .header("x-user-id", "candidate-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(startBody);
        String attemptId = root.path("data").path("attempt").path("id").asText();

        mockMvc.perform(post("/api/v1/attempts/" + attemptId + "/answers/auto-save")
                        .header("x-user-id", "candidate-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientRevision": 0,
                                  "answers": [
                                    {
                                      "questionId": "qv-demo-1",
                                      "type": "single_choice",
                                      "selectedOptionId": "b"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answerSheet.revision", is(1)));
    }
}
