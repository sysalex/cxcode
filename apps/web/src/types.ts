export type ExamStatus =
  | "DRAFT"
  | "SCHEDULED"
  | "LIVE"
  | "CLOSED"
  | "ARCHIVED"
  | "CANCELLED";
export type AttemptStatus =
  | "NOT_STARTED"
  | "IN_PROGRESS"
  | "SUBMITTED"
  | "TIMEOUT_SUBMITTED"
  | "INVALIDATED"
  | "ABANDONED";
export type QuestionType = "SINGLE_CHOICE" | "SHORT_TEXT";

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: {
    code: string;
    message: string;
    requestId: string;
    details: Record<string, unknown>;
  } | null;
  requestId: string;
}

export interface ExamSummary {
  id: string;
  title: string;
  status: ExamStatus;
  startAt: string;
  endAt: string;
  durationMinutes: number;
}

export interface CandidateExamList {
  exams: ExamSummary[];
  serverNow: string;
}

export interface QuestionOption {
  id: string;
  label: string;
}

export interface CandidateQuestion {
  id: string;
  versionId: string;
  type: QuestionType;
  prompt: string;
  points: number;
  options: QuestionOption[];
  maxLength: number | null;
}

export interface CandidatePaper {
  id: string;
  title: string;
  questions: CandidateQuestion[];
}

export interface Attempt {
  id: string;
  examId: string;
  candidateId: string;
  status: AttemptStatus;
  startedAt: string;
  endsAt: string;
  submittedAt: string | null;
  version: number;
}

export interface SavedAnswer {
  questionId: string;
  type: "single_choice" | "short_text";
  selectedOptionId?: string;
  text?: string;
}

export interface AnswerSheet {
  id: string;
  attemptId: string;
  status: "DRAFT" | "SUBMITTED" | "TIMEOUT_SUBMITTED" | "LOCKED";
  answers: SavedAnswer[];
  revision: number;
  updatedAt: string;
  submittedAt: string | null;
}

export interface AttemptView {
  attempt: Attempt;
  answerSheet: AnswerSheet;
  paper: CandidatePaper;
  serverNow: string;
}
