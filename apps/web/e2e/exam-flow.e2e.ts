import { expect, test } from "@playwright/test";

const attemptView = {
  attempt: {
    id: "attempt-e2e-1",
    examId: "exam-e2e-1",
    candidateId: "candidate-1",
    status: "IN_PROGRESS",
    startedAt: "2026-04-16T01:00:00.000Z",
    endsAt: "2099-04-16T02:00:00.000Z",
    submittedAt: null,
    version: 1,
  },
  answerSheet: {
    id: "sheet-e2e-1",
    attemptId: "attempt-e2e-1",
    status: "DRAFT",
    answers: [],
    revision: 0,
    updatedAt: "2026-04-16T01:00:00.000Z",
    submittedAt: null,
  },
  paper: {
    id: "paper-e2e-1",
    title: "E2E 基础能力试卷",
    questions: [
      {
        id: "q-e2e-1",
        versionId: "qv-e2e-1",
        type: "SINGLE_CHOICE",
        prompt: "在线考试中，判断考试是否超时应以哪个时间为准？",
        points: 5,
        options: [
          { id: "a", label: "考生浏览器时间" },
          { id: "b", label: "服务端时间" },
        ],
        maxLength: null,
      },
      {
        id: "q-e2e-2",
        versionId: "qv-e2e-2",
        type: "SHORT_TEXT",
        prompt: "请简述自动保存失败时系统应该怎么处理。",
        points: 10,
        options: [],
        maxLength: 300,
      },
    ],
  },
  serverNow: "2026-04-16T01:00:00.000Z",
};

test("考生可以开始作答、保存并交卷", async ({ page }) => {
  let savedRevision = 0;

  await page.route("**/api/v1/exams", async (route) => {
    await route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        data: {
          exams: [
            {
              id: "exam-e2e-1",
              title: "E2E 演示考试",
              status: "LIVE",
              startAt: "2026-04-16T01:00:00.000Z",
              endAt: "2099-04-16T02:00:00.000Z",
              durationMinutes: 60,
            },
          ],
          serverNow: "2026-04-16T01:00:00.000Z",
        },
        error: null,
        requestId: "req-e2e-exams",
      }),
    });
  });

  await page.route("**/api/v1/exams/exam-e2e-1/attempts", async (route) => {
    await route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        data: attemptView,
        error: null,
        requestId: "req-e2e-start",
      }),
    });
  });

  await page.route(
    "**/api/v1/attempts/attempt-e2e-1/answers/auto-save",
    async (route) => {
      const payload = route.request().postDataJSON() as {
        answers: Array<Record<string, string>>;
      };
      savedRevision += 1;
      await route.fulfill({
        contentType: "application/json",
        body: JSON.stringify({
          success: true,
          data: {
            ...attemptView,
            answerSheet: {
              ...attemptView.answerSheet,
              answers: payload.answers,
              revision: savedRevision,
              updatedAt: "2026-04-16T01:01:00.000Z",
            },
          },
          error: null,
          requestId: "req-e2e-save",
        }),
      });
    },
  );

  await page.route("**/api/v1/attempts/attempt-e2e-1/submit", async (route) => {
    await route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({
        success: true,
        data: {
          ...attemptView,
          attempt: {
            ...attemptView.attempt,
            status: "SUBMITTED",
            submittedAt: "2026-04-16T01:02:00.000Z",
          },
          answerSheet: {
            ...attemptView.answerSheet,
            status: "SUBMITTED",
            revision: savedRevision,
            submittedAt: "2026-04-16T01:02:00.000Z",
          },
        },
        error: null,
        requestId: "req-e2e-submit",
      }),
    });
  });

  await page.goto("/");

  await expect(
    page.getByRole("heading", { level: 2, name: "E2E 演示考试" }),
  ).toBeVisible();
  await page.getByRole("button", { name: "开始 / 恢复作答" }).click();

  await expect(page.getByRole("heading", { name: "进行中" })).toBeVisible();
  await page.getByText("服务端时间").click();
  await page
    .getByPlaceholder("请输入答案")
    .fill("提示用户、保留本地草稿，并支持安全重试。");
  await page.getByRole("button", { name: "立即保存" }).click();

  await expect(page.getByText("保存状态：已保存")).toBeVisible();
  await page.getByRole("button", { name: "交卷" }).click();

  await expect(page.getByRole("heading", { name: "已交卷" })).toBeVisible();
  await expect(page.getByPlaceholder("请输入答案")).toBeDisabled();
});
