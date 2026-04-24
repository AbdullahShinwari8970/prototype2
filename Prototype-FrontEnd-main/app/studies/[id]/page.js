"use client";

import React, { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { isAuthenticated } from '../../lib/auth';
import { getStudyById, getSurveyById, getParticipants, enrollParticipant, withdrawParticipant, addSurveyWithQuestions } from '../../lib/api';
import NavBar from '../../components/NavBar';

const SCHEDULE_TYPES = ['ONE_TIME', 'DAILY', 'WEEKLY', 'MONTHLY'];
const QUESTION_TYPES = ['TEXT'];

function emptyQuestion() { return { text: '', type: 'TEXT' }; }

export default function StudyDetailPage() {
  const router = useRouter();
  const { id } = useParams();

  const [study, setStudy] = useState(null);
  const [surveys, setSurveys] = useState([]);
  const [participants, setParticipants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Enroll form
  const [enrollName, setEnrollName] = useState('');
  const [enrollEmail, setEnrollEmail] = useState('');
  const [enrollLoading, setEnrollLoading] = useState(false);
  const [enrollError, setEnrollError] = useState('');
  const [enrollSuccess, setEnrollSuccess] = useState('');

  // Add survey form
  const [showSurveyForm, setShowSurveyForm] = useState(false);
  const [surveyName, setSurveyName] = useState('');
  const [surveySchedule, setSurveySchedule] = useState('ONE_TIME');
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
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  // ── Enroll ───────────────────────────────────────────────────────────────

  async function handleEnroll(e) {
    e.preventDefault();
    setEnrollError(''); setEnrollSuccess(''); setEnrollLoading(true);
    try {
      await enrollParticipant(id, enrollName, enrollEmail);
      setEnrollSuccess(`${enrollName} enrolled successfully.`);
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
        questions: surveyQuestions
      });
      // Reset form and reload surveys
      setSurveyName(''); setSurveySchedule('ONE_TIME'); setSurveyQuestions([emptyQuestion()]);
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

  const scheduleColour = {
    ONE_TIME: 'text-blue-300 bg-blue-400/10',
    DAILY:    'text-green-300 bg-green-400/10',
    WEEKLY:   'text-purple-300 bg-purple-400/10',
    MONTHLY:  'text-yellow-300 bg-yellow-400/10',
  };

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
          <h1 className="text-3xl font-bold text-white">{study?.name}</h1>
          <p className="mt-1 text-rose-300 text-sm">
            {surveys.length} survey{surveys.length !== 1 ? 's' : ''} · {participants.length} participant{participants.length !== 1 ? 's' : ''}
          </p>
        </div>

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
                <div className="flex gap-2 flex-wrap">
                  {SCHEDULE_TYPES.map(type => (
                    <button key={type} type="button" onClick={() => setSurveySchedule(type)}
                      className={`rounded-xl px-4 py-2 text-xs font-medium ring-1 transition-all ${
                        surveySchedule === type ? 'bg-rose-600 text-white ring-rose-500' : 'bg-white/5 text-rose-300 ring-rose-300/20 hover:bg-white/10'
                      }`}>
                      {type}
                    </button>
                  ))}
                </div>
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
          <h2 className="text-lg font-semibold text-white mb-4">Enroll a Participant</h2>
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
