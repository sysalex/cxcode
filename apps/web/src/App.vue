<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { autoSaveAttempt, listExams, startAttempt, submitAttempt } from "./api";
import type {
  AttemptView,
  CandidateQuestion,
  ExamSummary,
  SavedAnswer,
} from "./types";

const exams = ref<ExamSummary[]>([]);
const activeAttempt = ref<AttemptView | null>(null);
const selectedAnswers = ref<Record<string, string>>({});
const textAnswers = ref<Record<string, string>>({});
const loading = ref(false);
const saving = ref(false);
const saveStatus = ref("尚未保存");
const lastSavedAt = ref<string | null>(null);

const activeExam = computed(() =>
  activeAttempt.value
    ? exams.value.find(
        (exam) => exam.id === activeAttempt.value?.attempt.examId,
      )
    : exams.value[0],
);

const isSubmitted = computed(() => {
  const status = activeAttempt.value?.attempt.status;
  return status === "SUBMITTED" || status === "TIMEOUT_SUBMITTED";
});

const remainingText = computed(() => {
  const endsAt = activeAttempt.value?.attempt.endsAt;
  if (!endsAt) {
    return "未开始";
  }
  const remainingMs = new Date(endsAt).getTime() - Date.now();
  if (remainingMs <= 0) {
    return "已到时";
  }
  const minutes = Math.floor(remainingMs / 60000);
  const seconds = Math.floor((remainingMs % 60000) / 1000);
  return `${minutes} 分 ${seconds.toString().padStart(2, "0")} 秒`;
});

onMounted(async () => {
  await loadExams();
  window.setInterval(() => {
    if (activeAttempt.value && !isSubmitted.value) {
      void saveAnswers();
    }
  }, 15000);
});

async function loadExams(): Promise<void> {
  loading.value = true;
  try {
    const result = await listExams();
    exams.value = result.exams;
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "加载考试失败");
  } finally {
    loading.value = false;
  }
}

async function start(examId: string): Promise<void> {
  loading.value = true;
  try {
    const view = await startAttempt(examId);
    applyAttemptView(view);
    ElMessage.success("已进入考试");
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "进入考试失败");
  } finally {
    loading.value = false;
  }
}

async function saveAnswers(): Promise<void> {
  if (!activeAttempt.value || isSubmitted.value || saving.value) {
    return;
  }

  saving.value = true;
  saveStatus.value = "保存中";
  try {
    const view = await autoSaveAttempt({
      attemptId: activeAttempt.value.attempt.id,
      clientRevision: activeAttempt.value.answerSheet.revision,
      answers: collectAnswers(activeAttempt.value.paper.questions),
    });
    applyAttemptView(view);
    lastSavedAt.value = new Date().toLocaleTimeString();
    saveStatus.value = "已保存";
  } catch (error) {
    saveStatus.value = "保存失败";
    ElMessage.error(error instanceof Error ? error.message : "自动保存失败");
  } finally {
    saving.value = false;
  }
}

async function submit(): Promise<void> {
  if (!activeAttempt.value) {
    return;
  }
  await saveAnswers();
  loading.value = true;
  try {
    const view = await submitAttempt({
      attemptId: activeAttempt.value.attempt.id,
      idempotencyKey: `submit-${activeAttempt.value.attempt.id}`,
    });
    applyAttemptView(view);
    ElMessage.success("交卷成功");
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "交卷失败");
  } finally {
    loading.value = false;
  }
}

function applyAttemptView(view: AttemptView): void {
  activeAttempt.value = view;
  selectedAnswers.value = {};
  textAnswers.value = {};

  for (const answer of view.answerSheet.answers) {
    if (answer.type === "single_choice" && answer.selectedOptionId) {
      selectedAnswers.value[answer.questionId] = answer.selectedOptionId;
    }
    if (answer.type === "short_text" && typeof answer.text === "string") {
      textAnswers.value[answer.questionId] = answer.text;
    }
  }
}

function collectAnswers(questions: CandidateQuestion[]): SavedAnswer[] {
  const answers: SavedAnswer[] = [];

  for (const question of questions) {
    if (question.type === "SINGLE_CHOICE") {
      const selectedOptionId = selectedAnswers.value[question.versionId];
      if (selectedOptionId) {
        answers.push({
          questionId: question.versionId,
          type: "single_choice",
          selectedOptionId,
        });
      }
    } else {
      answers.push({
        questionId: question.versionId,
        type: "short_text",
        text: textAnswers.value[question.versionId] ?? "",
      });
    }
  }

  return answers;
}
</script>

<template>
  <main class="exam-shell">
    <section class="exam-topbar">
      <div>
        <p class="eyebrow">在线考试</p>
        <h1>{{ activeExam?.title ?? "考试中心" }}</h1>
      </div>
      <img
        class="topbar-image"
        src="https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=320&q=80"
        alt="打开的书本和学习桌面"
      />
    </section>

    <section v-if="!activeAttempt" class="exam-list" v-loading="loading">
      <article v-for="exam in exams" :key="exam.id" class="exam-item">
        <div>
          <h2>{{ exam.title }}</h2>
          <p>
            考试时间：{{ new Date(exam.startAt).toLocaleString() }} 至
            {{ new Date(exam.endAt).toLocaleString() }}
          </p>
          <p>答题时长：{{ exam.durationMinutes }} 分钟</p>
        </div>
        <el-button
          type="primary"
          :disabled="exam.status !== 'LIVE'"
          @click="start(exam.id)"
        >
          开始 / 恢复作答
        </el-button>
      </article>
    </section>

    <section v-else class="attempt-layout">
      <aside class="attempt-status">
        <p class="eyebrow">作答状态</p>
        <h2>{{ isSubmitted ? "已交卷" : "进行中" }}</h2>
        <p>剩余时间：{{ remainingText }}</p>
        <p>保存状态：{{ saveStatus }}</p>
        <p v-if="lastSavedAt">上次保存：{{ lastSavedAt }}</p>
        <el-button :disabled="isSubmitted || saving" @click="saveAnswers"
          >立即保存</el-button
        >
        <el-button type="danger" :disabled="isSubmitted" @click="submit"
          >交卷</el-button
        >
      </aside>

      <section class="question-list">
        <article
          v-for="(question, index) in activeAttempt.paper.questions"
          :key="question.versionId"
          class="question-item"
        >
          <div class="question-title">
            <span>第 {{ index + 1 }} 题</span>
            <strong>{{ question.points }} 分</strong>
          </div>
          <h2>{{ question.prompt }}</h2>

          <el-radio-group
            v-if="question.type === 'SINGLE_CHOICE'"
            v-model="selectedAnswers[question.versionId]"
            :disabled="isSubmitted"
          >
            <el-radio
              v-for="option in question.options"
              :key="option.id"
              :value="option.id"
              size="large"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>

          <el-input
            v-else
            v-model="textAnswers[question.versionId]"
            :maxlength="question.maxLength ?? 300"
            :disabled="isSubmitted"
            type="textarea"
            :rows="6"
            show-word-limit
            placeholder="请输入答案"
          />
        </article>
      </section>
    </section>
  </main>
</template>
