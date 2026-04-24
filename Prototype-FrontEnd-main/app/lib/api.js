import { getToken } from './auth';

const API_BASE = process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8080";

function authHeaders() {
  return {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${getToken()}`
  };
}

// ── Studies ───────────────────────────────────────────────────────────────────

export async function getStudies(researcherId) {
  const res = await fetch(`${API_BASE}/api/studies?researcherId=${researcherId}`, {
    headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to fetch studies");
  return data;
}

export async function getStudyById(id) {
  const res = await fetch(`${API_BASE}/api/studies/${id}`, {
    headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to fetch study");
  return data;
}

export async function createFullStudy(studyData) {
  const res = await fetch(`${API_BASE}/api/studies/full`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify(studyData)
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to create study");
  return data;
}

// ── Surveys ───────────────────────────────────────────────────────────────────

export async function getSurveyById(surveyId) {
  const res = await fetch(`${API_BASE}/api/surveys/${surveyId}`, {
    headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to fetch survey");
  return data;
}

// ── Participants ──────────────────────────────────────────────────────────────

export async function getParticipants(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/participants`, {
    headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to fetch participants");
  return data;
}

export async function enrollParticipant(studyId, name, email) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/participants`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ name, email })
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to enroll participant");
  return data;
}

export async function withdrawParticipant(studyId, enrollmentId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/participants/${enrollmentId}`, {
    method: "DELETE",
    headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to withdraw participant");
  return data;
}

export async function addSurveyWithQuestions(studyId, surveyData) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/surveys`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify(surveyData)
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to add survey");
  return data;
}
