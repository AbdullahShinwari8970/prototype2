"use client";

import React, { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { isAuthenticated } from '../../lib/auth';
import { getStudyById, getSurveyById, getParticipants, enrollParticipant, withdrawParticipant, addSurveyWithQuestions, deployStudy, pauseStudy, resumeStudy, revertStudy, closeStudy, exportStudyCsv, exportStudyJson, getCompliance } from '../../lib/api';
import NavBar from '../../components/NavBar';

const SCHEDULE_TYPES = ['ONE_TIME', 'DAILY', 'WEEKLY', 'MONTHLY'];
const RECURRING = ['DAILY', 'WEEKLY', 'MONTHLY'];
const QUESTION_TYPES = ['TEXT'];

function formatHour(h) {
  if (h === 0)  return '12:00 AM';
  if (h < 12)  return `${h}:00 AM`;
  if (h === 12) return '12:00 PM';
  return `${h - 12}:00 PM`;
}

function emptyQuestion() { return { text: '', type: 'TEXT' }; }

export default function StudyDetailPage() {
  const router = useRouter();
  const { id } = useParams();

  const [study, setStudy] = useState(null);
  const [surveys, setSurveys] = useState([]);
  const [participants, setParticipants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Status actions
  const [statusLoading, setStatusLoading] = useState(false);

  // Enroll form
  const [enrollName, setEnrollName] = useState('');
  const [enrollEmail, setEnrollEmail] = useState('');
  const [enrollLoading, setEnrollLoading] = useState(false);
  const [enrollError, setEnrollError] = useState('');
  const [enrollSuccess, setEnrollSuccess] = useState('');

  // Compliance
  const [compliance, setCompliance] = useState([]);

  // Add survey form
  const [showSurveyForm, setShowSurveyForm] = useState(false);
  const [surveyName, setSurveyName] = useState('');
  const [surveySchedule, setSurveySchedule] = useState('ONE_TIME');
  const [surveySendHour, setSurveySendHour] = useState(9);
  const [surveyQuestions, setSurveyQuestions] = useState([emptyQuestion()]);
  const [surveyLoading, setSurveyLoading] = useState(false);
  const [surveyError, setSurveyError] = useState('');

  useEffect(() => {
    if (!isAuthenticated()) { router.replace('/login'); return; }
    loadStudy();
  }, [id]);

  async function loadStudy() {
    try {
      setLoading(true);
      const studyData = await getStudyById(id);
      setStudy(studyData);
      const surveyDetails = await Promise.all(studyData.surveys.map(s => getSurveyById(s.id)));
      setSurveys(surveyDetails);
      setParticipants(await getParticipants(id));
      try { setCompliance(await getCompliance(id)); } catch (_) {}
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  // ── Status actions ───────────────────────────────────────────────────────

  async function handleStatusAction(action, confirmMsg) {
    if (confirmMsg && !confirm(confirmMsg)) return;
    setStatusLoading(true);
    try {
      const fn = { deploy: deployStudy, pause: pauseStudy, resume: resumeStudy, revert: revertStudy, close: closeStudy }[action];
      const updated = await fn(id);
      setStudy(updated);
    } catch (err) {
      alert(err.message);
    } finally {
      setStatusLoading(false);
    }
  }

  // ── Enroll ───────────────────────────────────────────────────────────────

  async function handleEnroll(e) {
    e.preventDefault();
    setEnrollError(''); setEnrollSuccess(''); setEnrollLoading(true);
    try {
      await enrollParticipant(id, enrollName, enrollEmail);
      setEnrollSuccess(`${enrollName} enrolled successfully.${isActive ? ' Survey link sent.' : ''}`);
      setEnrollName(''); setEnrollEmail('');
      setParticipants(await getParticipants(id));
    } catch (err) {
      setEnrollError(err.message);
    } finally {
      setEnrollLoading(false);
    }
  }

  // ── Withdraw ─────────────────────────────────────────────────────────────

  async function handleWithdraw(enrollmentId, participantName) {
    if (!confirm(`Withdraw ${participantName} from this study?`)) return;
    try {
      await withdrawParticipant(id, enrollmentId);
      setParticipants(await getParticipants(id));
    } catch (err) {
      alert(err.message);
    }
  }

  // ── Add Survey ───────────────────────────────────────────────────────────

  function addQuestion() { setSurveyQuestions(prev => [...prev, emptyQuestion()]); }
  function removeQuestion(qi) { setSurveyQuestions(prev => prev.filter((_, i) => i !== qi)); }
  function updateQuestion(qi, field, value) {
    setSurveyQuestions(prev => prev.map((q, i) => i === qi ? { ...q, [field]: value } : q));
  }

  async function handleAddSurvey(e) {
    e.preventDefault();
    setSurveyError(''); setSurveyLoading(true);
    try {
      await addSurveyWithQuestions(id, {
        name: surveyName,
        scheduleType: surveySchedule,
        sendHour: RECURRING.includes(surveySchedule) ? surveySendHour : null,
        questions: surveyQuestions
      });
      setSurveyName(''); setSurveySchedule('ONE_TIME'); setSurveySendHour(9); setSurveyQuestions([emptyQuestion()]);
      setShowSurveyForm(false);
      const studyData = await getStudyById(id);
      const surveyDetails = await Promise.all(studyData.surveys.map(s => getSurveyById(s.id)));
      setSurveys(surveyDetails);
    } catch (err) {
      setSurveyError(err.message);
    } finally {
      setSurveyLoading(false);
    }
  }

  async function handleExportCsv() {
    try {
      const blob = await exportStudyCsv(id);
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `study-${id}-export.csv`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      alert(err.message);
    }
  }

  async function handleExportJson() {
    try {
      const data = await exportStudyJson(id);
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `study-${id}-export.json`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      alert(err.message);
    }
  }

  const scheduleColour = {
    INSTANT:  'text-rose-300 bg-rose-400/10',
    ONE_TIME: 'text-blue-300 bg-blue-400/10',
    DAILY:    'text-green-300 bg-green-400/10',
    WEEKLY:   'text-purple-300 bg-purple-400/10',
    MONTHLY:  'text-yellow-300 bg-yellow-400/10',
  };

  const status = study?.status ?? 'DRAFT';
  const isActive = status === 'ACTIVE';
  const isPaused = status === 'PAUSED';
  const isClosed = status === 'CLOSED';
  const isDraft  = status === 'DRAFT';

  const statusConfig = {
    DRAFT:  { label: 'Draft',  dot: 'bg-zinc-400',   text: 'text-zinc-400',  ring: 'ring-zinc-400/20',  bg: 'bg-zinc-400/10' },
    ACTIVE: { label: 'Live',   dot: 'bg-green-400',  text: 'text-green-300', ring: 'ring-green-400/20', bg: 'bg-green-400/10' },
    PAUSED: { label: 'Paused', dot: 'bg-yellow-400', text: 'text-yellow-300',ring: 'ring-yellow-400/20',bg: 'bg-yellow-400/10' },
    CLOSED: { label: 'Closed', dot: 'bg-rose-500',   text: 'text-rose-400',  ring: 'ring-rose-400/20',  bg: 'bg-rose-400/10' },
  }[status] ?? {};

  if (loading) return (
    <div className="min-h-screen bg-rose-950 flex items-center justify-center">
      <svg className="animate-spin h-8 w-8 text-rose-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
    </div>
  );

  if (error) return (
    <div className="min-h-screen bg-rose-950 text-rose-50 font-sans">
      <NavBar />
      <div className="max-w-3xl mx-auto px-4 py-10">
        <div className="rounded-xl bg-red-500/10 border border-red-500/20 px-4 py-3 text-sm text-red-300">{error}</div>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-rose-950 text-rose-50 font-sans">
      <NavBar />

      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">

        {/* Header */}
        <div>
          <button onClick={() => router.push('/')} className="text-xs text-rose-400 hover:text-rose-200 mb-3 transition-colors">
            ← Back to studies
          </button>
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold text-white">{study?.name}</h1>
            <span className={`inline-flex items-center gap-1.5 text-xs font-medium rounded-full px-3 py-1 ring-1 ${statusConfig.bg} ${statusConfig.text} ${statusConfig.ring}`}>
              <span className={`w-1.5 h-1.5 rounded-full ${statusConfig.dot}`} />
              {statusConfig.label}
            </span>
          </div>
          <p className="mt-1 text-rose-300 text-sm">
            {surveys.length} survey{surveys.length !== 1 ? 's' : ''} · {participants.length} participant{participants.length !== 1 ? 's' : ''}
          </p>
        </div>

        {/* Status action bar */}
        {!isClosed && (
          <div className="rounded-2xl border border-rose-800/40 bg-white/5 p-5">
            <div className="flex items-center justify-between gap-4 flex-wrap">
              <div>
                <p className="text-sm font-medium text-white">
                  {isDraft  && 'Ready to launch?'}
                  {isActive && 'Study is live'}
                  {isPaused && 'Study is paused'}
                </p>
                <p className="text-xs text-rose-400 mt-0.5">
                  {isDraft  && 'Deploy when you have enrolled your participants. Survey links will be sent immediately.'}
                  {isActive && 'Survey links are being sent on schedule. You can pause or close the study at any time.'}
                  {isPaused && 'Token sending is suspended. Resume to continue the study or revert to draft to reconfigure.'}
                </p>
              </div>

              <div className="flex items-center gap-2 shrink-0">
                {statusLoading && (
                  <svg className="animate-spin h-4 w-4 text-rose-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                )}

                {/* DRAFT — Deploy */}
                {isDraft && (
                  <button
                    onClick={() => handleStatusAction('deploy', `Deploy "${study.name}"?\n\nSurvey links will be sent to all ${participants.filter(p => p.status === 'ACTIVE').length} enrolled participant(s) immediately.`)}
                    disabled={statusLoading || participants.filter(p => p.status === 'ACTIVE').length === 0}
                    title={participants.filter(p => p.status === 'ACTIVE').length === 0 ? 'Enrol at least one participant before deploying' : ''}
                    className="rounded-xl bg-rose-600 hover:bg-rose-500 px-5 py-2.5 text-sm font-semibold text-white transition-all disabled:opacity-40 disabled:cursor-not-allowed"
                  >
                    Deploy Study
                  </button>
                )}

                {/* ACTIVE — Pause + Close */}
                {isActive && (
                  <>
                    <button
                      onClick={() => handleStatusAction('pause')}
                      disabled={statusLoading}
                      className="rounded-xl bg-white/5 ring-1 ring-inset ring-rose-300/20 hover:bg-white/10 px-4 py-2.5 text-sm font-medium text-rose-200 transition-all disabled:opacity-40"
                    >
                      Pause
                    </button>
                    <button
                      onClick={() => handleStatusAction('close', `Close "${study.name}" permanently?\n\nNo more survey links will be sent.`)}
                      disabled={statusLoading}
                      className="rounded-xl bg-white/5 ring-1 ring-inset ring-rose-500/30 hover:bg-rose-500/10 px-4 py-2.5 text-sm font-medium text-rose-400 transition-all disabled:opacity-40"
                    >
                      Close Study
                    </button>
                  </>
                )}

                {/* PAUSED — Resume + Revert + Close */}
                {isPaused && (
                  <>
                    <button
                      onClick={() => handleStatusAction('resume')}
                      disabled={statusLoading}
                      className="rounded-xl bg-rose-600 hover:bg-rose-500 px-4 py-2.5 text-sm font-semibold text-white transition-all disabled:opacity-40"
                    >
                      Resume
                    </button>
                    <button
                      onClick={() => handleStatusAction('revert', 'Revert to draft? The study will stop sending tokens.')}
                      disabled={statusLoading}
                      className="rounded-xl bg-white/5 ring-1 ring-inset ring-rose-300/20 hover:bg-white/10 px-4 py-2.5 text-sm font-medium text-rose-200 transition-all disabled:opacity-40"
                    >
                      Revert to Draft
                    </button>
                    <button
                      onClick={() => handleStatusAction('close', `Close "${study.name}" permanently?`)}
                      disabled={statusLoading}
                      className="rounded-xl bg-white/5 ring-1 ring-inset ring-rose-500/30 hover:bg-rose-500/10 px-4 py-2.5 text-sm font-medium text-rose-400 transition-all disabled:opacity-40"
                    >
                      Close Study
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Closed banner */}
        {isClosed && (
          <div className="rounded-xl border border-rose-500/20 bg-rose-500/5 px-4 py-3 text-sm text-rose-400">
            This study is closed. No further survey links will be sent.
          </div>
        )}

        {/* Export */}
        <section className="rounded-2xl border border-rose-800/40 bg-white/5 p-5">
          <div className="flex items-center justify-between flex-wrap gap-4">
            <div>
              <h2 className="text-sm font-semibold text-white">Export Data</h2>
              <p className="text-xs text-rose-400 mt-0.5">Download all collected responses for this study.</p>
            </div>
            <div className="flex gap-2">
              <button
                onClick={handleExportCsv}
                className="rounded-xl bg-white/5 ring-1 ring-inset ring-rose-300/20 hover:bg-white/10 px-4 py-2 text-sm font-medium text-rose-200 transition-all"
              >
                Download CSV
              </button>
              <button
                onClick={handleExportJson}
                className="rounded-xl bg-white/5 ring-1 ring-inset ring-rose-300/20 hover:bg-white/10 px-4 py-2 text-sm font-medium text-rose-200 transition-all"
              >
                Download JSON
              </button>
            </div>
          </div>
        </section>

        {/* Compliance */}
        <section className="rounded-2xl border border-rose-800/40 bg-white/5 p-5">
          <h2 className="text-sm font-semibold text-white mb-4">Compliance Overview</h2>
          {compliance.length === 0 ? (
            <p className="text-xs text-rose-400">No responses yet.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-xs text-rose-400 border-b border-rose-800/40">
                    <th className="pb-2 pr-4 font-medium">Participant</th>
                    <th className="pb-2 pr-4 font-medium">Survey</th>
                    <th className="pb-2 pr-4 font-medium text-right">Sent</th>
                    <th className="pb-2 pr-4 font-medium text-right">Completed</th>
                    <th className="pb-2 font-medium text-right">Rate</th>
                  </tr>
                </thead>
                <tbody>
                  {compliance.map((row, i) => (
                    <tr key={i} className="border-b border-rose-800/20 last:border-0">
                      <td className="py-2 pr-4 text-rose-100">{row.participantName}</td>
                      <td className="py-2 pr-4 text-rose-300">{row.surveyName}</td>
                      <td className="py-2 pr-4 text-rose-300 text-right">{row.sent}</td>
                      <td className="py-2 pr-4 text-rose-300 text-right">{row.completed}</td>
                      <td className="py-2 text-right text-rose-100 font-medium">{row.completionRate}%</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        {/* Surveys */}
        <section>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-white">Surveys</h2>
            <button
              onClick={() => setShowSurveyForm(v => !v)}
              className="text-sm rounded-xl bg-white/5 px-4 py-2 text-rose-300 ring-1 ring-inset ring-rose-300/20 hover:bg-white/10 hover:text-white transition-all"
            >
              {showSurveyForm ? 'Cancel' : '+ Add Survey'}
            </button>
          </div>

          {/* Add Survey Form */}
          {showSurveyForm && (
            <form onSubmit={handleAddSurvey} className="rounded-2xl border border-rose-600/30 bg-white/5 p-5 mb-4 space-y-4">
              <h3 className="text-sm font-semibold text-white">New Survey</h3>

              <div>
                <label className="block text-xs font-medium text-rose-300 mb-1">Survey Name</label>
                <input
                  type="text" required value={surveyName}
                  onChange={e => setSurveyName(e.target.value)}
                  placeholder="e.g. Evening Check-in"
                  className="block w-full rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 placeholder:text-rose-300/50 outline-none transition-all sm:text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-rose-300 mb-2">Schedule</label>
                <div className="flex gap-2 flex-wrap mb-3">
                  {SCHEDULE_TYPES.map(type => (
                    <button key={type} type="button" onClick={() => setSurveySchedule(type)}
                      className={`rounded-xl px-4 py-2 text-xs font-medium ring-1 transition-all ${
                        surveySchedule === type ? 'bg-rose-600 text-white ring-rose-500' : 'bg-white/5 text-rose-300 ring-rose-300/20 hover:bg-white/10'
                      }`}>
                      {type}
                    </button>
                  ))}
                </div>
                {RECURRING.includes(surveySchedule) && (
                  <div className="flex items-center gap-3">
                    <label className="text-xs font-medium text-rose-300">Send time</label>
                    <select
                      value={surveySendHour}
                      onChange={e => setSurveySendHour(parseInt(e.target.value))}
                      className="rounded-xl border-0 py-2 px-3 bg-rose-900/50 text-rose-200 ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 outline-none sm:text-sm"
                    >
                      {Array.from({ length: 24 }, (_, h) => (
                        <option key={h} value={h}>{formatHour(h)}</option>
                      ))}
                    </select>
                  </div>
                )}
              </div>

              <div className="space-y-2">
                <label className="block text-xs font-medium text-rose-300">Questions</label>
                {surveyQuestions.map((q, qi) => (
                  <div key={qi} className="flex gap-3 items-center">
                    <input
                      type="text" required value={q.text}
                      onChange={e => updateQuestion(qi, 'text', e.target.value)}
                      placeholder={`Question ${qi + 1}`}
                      className="flex-1 rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 placeholder:text-rose-300/50 outline-none transition-all sm:text-sm"
                    />
                    <select value={q.type} onChange={e => updateQuestion(qi, 'type', e.target.value)}
                      className="rounded-xl border-0 py-2.5 px-3 bg-rose-900/50 text-rose-200 ring-1 ring-inset ring-rose-300/20 outline-none sm:text-sm">
                      {QUESTION_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
                    </select>
                    {surveyQuestions.length > 1 && (
                      <button type="button" onClick={() => removeQuestion(qi)} className="text-rose-500 hover:text-red-400 text-lg transition-colors">×</button>
                    )}
                  </div>
                ))}
                <button type="button" onClick={addQuestion} className="text-xs text-rose-400 hover:text-rose-200 transition-colors">
                  + Add question
                </button>
              </div>

              {surveyError && <p className="text-sm text-red-300">{surveyError}</p>}

              <button type="submit" disabled={surveyLoading}
                className="w-full rounded-xl bg-gradient-to-r from-rose-500 to-rose-600 py-2.5 text-sm font-semibold text-white hover:from-rose-400 hover:to-rose-500 transition-all disabled:opacity-50">
                {surveyLoading ? 'Adding...' : 'Add Survey'}
              </button>
            </form>
          )}

          {surveys.length === 0 ? (
            <p className="text-rose-400 text-sm">No surveys yet.</p>
          ) : (
            <div className="space-y-4">
              {surveys.map(survey => (
                <div key={survey.id} className="rounded-2xl border border-rose-800/40 bg-white/5 p-5">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="font-semibold text-white">{survey.name}</h3>
                    <span className={`text-xs font-medium rounded-full px-3 py-1 ${scheduleColour[survey.scheduleType] || 'text-rose-300 bg-rose-400/10'}`}>
                      {survey.scheduleType}
                    </span>
                  </div>
                  {survey.questions?.length > 0 ? (
                    <ul className="space-y-2">
                      {survey.questions.map((q, idx) => (
                        <li key={q.id} className="flex items-start gap-3 text-sm">
                          <span className="mt-0.5 w-5 h-5 rounded-full bg-rose-600/20 border border-rose-600/30 flex items-center justify-center text-xs text-rose-400 shrink-0">{idx + 1}</span>
                          <div>
                            <span className="text-rose-100">{q.text}</span>
                            <span className="ml-2 text-xs text-rose-500">[{q.type}]</span>
                          </div>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-rose-400 text-sm">No questions.</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </section>

        {/* Enroll Participant */}
        <section className="rounded-2xl border border-rose-800/40 bg-white/5 p-6">
          <h2 className="text-lg font-semibold text-white mb-1">Enroll a Participant</h2>
          {isActive && (
            <p className="text-xs text-green-300 mb-4">Study is live — survey links will be sent immediately on enrolment.</p>
          )}
          {!isActive && (
            <p className="text-xs text-rose-400 mb-4">Study is in draft — participants won't receive anything until you deploy.</p>
          )}
          <form onSubmit={handleEnroll} className="flex gap-3 flex-wrap items-end">
            <div className="flex-1 min-w-40">
              <label className="block text-xs font-medium text-rose-300 mb-1">Name</label>
              <input type="text" required value={enrollName} onChange={e => setEnrollName(e.target.value)}
                placeholder="Jane Doe"
                className="block w-full rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 placeholder:text-rose-300/50 outline-none transition-all sm:text-sm" />
            </div>
            <div className="flex-1 min-w-48">
              <label className="block text-xs font-medium text-rose-300 mb-1">Email</label>
              <input type="email" required value={enrollEmail} onChange={e => setEnrollEmail(e.target.value)}
                placeholder="jane@example.com"
                className="block w-full rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 placeholder:text-rose-300/50 outline-none transition-all sm:text-sm" />
            </div>
            <button type="submit" disabled={enrollLoading}
              className="rounded-xl bg-gradient-to-r from-rose-500 to-rose-600 px-5 py-2.5 text-sm font-semibold text-white hover:from-rose-400 hover:to-rose-500 transition-all disabled:opacity-50">
              {enrollLoading ? 'Enrolling...' : 'Enroll'}
            </button>
          </form>
          {enrollError && <p className="mt-3 text-sm text-red-300">{enrollError}</p>}
          {enrollSuccess && <p className="mt-3 text-sm text-green-300">{enrollSuccess}</p>}
        </section>

        {/* Participants Table */}
        <section>
          <h2 className="text-lg font-semibold text-white mb-4">Enrolled Participants</h2>
          {participants.length === 0 ? (
            <p className="text-rose-400 text-sm">No participants enrolled yet.</p>
          ) : (
            <div className="rounded-2xl border border-rose-800/40 bg-white/5 overflow-hidden">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-rose-800/40">
                    <th className="text-left px-5 py-3 text-xs font-medium text-rose-400 uppercase tracking-wider">Name</th>
                    <th className="text-left px-5 py-3 text-xs font-medium text-rose-400 uppercase tracking-wider">Email</th>
                    <th className="text-left px-5 py-3 text-xs font-medium text-rose-400 uppercase tracking-wider">Status</th>
                    <th className="text-left px-5 py-3 text-xs font-medium text-rose-400 uppercase tracking-wider">Enrolled</th>
                    <th className="px-5 py-3"></th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-rose-800/30">
                  {participants.map(p => (
                    <tr key={p.enrollmentId} className="hover:bg-white/5 transition-colors">
                      <td className="px-5 py-3 text-rose-100 font-medium">{p.participantName}</td>
                      <td className="px-5 py-3 text-rose-300">{p.participantEmail}</td>
                      <td className="px-5 py-3">
                        <span className={`text-xs rounded-full px-2 py-1 ${p.status === 'ACTIVE' ? 'text-green-300 bg-green-400/10' : 'text-rose-400 bg-rose-400/10'}`}>
                          {p.status}
                        </span>
                      </td>
                      <td className="px-5 py-3 text-rose-400">
                        {p.enrolledAt ? new Date(p.enrolledAt).toLocaleDateString() : '—'}
                      </td>
                      <td className="px-5 py-3 text-right">
                        {p.status === 'ACTIVE' && (
                          <button
                            onClick={() => handleWithdraw(p.enrollmentId, p.participantName)}
                            className="text-xs text-rose-500 hover:text-red-400 transition-colors"
                          >
                            Withdraw
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

      </main>
    </div>
  );
}
