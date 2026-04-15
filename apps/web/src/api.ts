import type {
  ApiResponse,
  AttemptView,
  CandidateExamList,
  SavedAnswer,
} from "./types";

const headers = {
  "content-type": "application/json",
  "x-user-id": "candidate-1",
};

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    ...init,
    headers: {
      ...headers,
      ...(init?.headers ?? {}),
    },
  });
  const body = (await response.json()) as ApiResponse<T>;
  if (!response.ok || !body.success || body.data === null) {
    throw new Error(body.error?.message ?? "请求失败");
  }
  return body.data;
}

export function listExams(): Promise<CandidateExamList> {
  return request<CandidateExamList>("/api/v1/exams");
}

export function startAttempt(examId: string): Promise<AttemptView> {
  return request<AttemptView>(`/api/v1/exams/${examId}/attempts`, {
    method: "POST",
  });
}

export function autoSaveAttempt(input: {
  attemptId: string;
  clientRevision: number;
  answers: SavedAnswer[];
}): Promise<AttemptView> {
  return request<AttemptView>(
    `/api/v1/attempts/${input.attemptId}/answers/auto-save`,
    {
      method: "POST",
      body: JSON.stringify({
        clientRevision: input.clientRevision,
        answers: input.answers,
      }),
    },
  );
}

export function submitAttempt(input: {
  attemptId: string;
  idempotencyKey: string;
}): Promise<AttemptView> {
  return request<AttemptView>(`/api/v1/attempts/${input.attemptId}/submit`, {
    method: "POST",
    body: JSON.stringify({
      idempotencyKey: input.idempotencyKey,
    }),
  });
}
