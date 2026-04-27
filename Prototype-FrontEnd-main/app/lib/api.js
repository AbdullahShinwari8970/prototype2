import { getToken } from './auth';

const API_BASE = process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8080";

function authHeaders() {
  return {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${getToken()}`
  };
}

// Studies
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

// Surveys

export async function getSurveyById(surveyId) {
  const res = await fetch(`${API_BASE}/api/surveys/${surveyId}`, {
    headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to fetch survey");
  return data;
}

// Participants

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

export async function deployStudy(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/deploy`, {
    method: "POST", headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to deploy study");
  return data;
}

export async function pauseStudy(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/pause`, {
    method: "POST", headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to pause study");
  return data;
}

export async function resumeStudy(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/resume`, {
    method: "POST", headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to resume study");
  return data;
}

export async function revertStudy(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/revert`, {
    method: "POST", headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to revert study");
  return data;
}

export async function closeStudy(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/close`, {
    method: "POST", headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to close study");
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

export async function getCompliance(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/compliance`, {
    headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to fetch compliance");
  return data;
}

export async function exportStudyCsv(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/export/csv`, {
    headers: authHeaders()
  });
  if (!res.ok) {
    const data = await res.json();
    throw new Error(data.error || "Failed to export CSV");
  }
  return res.blob();
}

export async function exportStudyJson(studyId) {
  const res = await fetch(`${API_BASE}/api/studies/${studyId}/export/json`, {
    headers: authHeaders()
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to export JSON");
  return data;
}
