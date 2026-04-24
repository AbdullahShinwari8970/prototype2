"use client";

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { isAuthenticated, getUser } from '../../lib/auth';
import { createFullStudy } from '../../lib/api';
import NavBar from '../../components/NavBar';

const SCHEDULE_TYPES = ['ONE_TIME', 'DAILY', 'WEEKLY', 'MONTHLY'];
const QUESTION_TYPES = ['TEXT'];

function emptyQuestion() {
  return { text: '', type: 'TEXT' };
}

function emptySurvey() {
  return { name: '', scheduleType: 'ONE_TIME', questions: [emptyQuestion()] };
}

export default function CreateStudyPage() {
  const router = useRouter();
  const [studyName, setStudyName] = useState('');
  const [surveys, setSurveys] = useState([emptySurvey()]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAuthenticated()) router.replace('/login');
  }, [router]);

  // ── Survey handlers ──────────────────────────────────────────────────────

  function addSurvey() {
    setSurveys(prev => [...prev, emptySurvey()]);
  }

  function removeSurvey(si) {
    setSurveys(prev => prev.filter((_, i) => i !== si));
  }

  function updateSurvey(si, field, value) {
    setSurveys(prev => prev.map((s, i) => i === si ? { ...s, [field]: value } : s));
  }

  // ── Question handlers ────────────────────────────────────────────────────

  function addQuestion(si) {
    setSurveys(prev => prev.map((s, i) =>
      i === si ? { ...s, questions: [...s.questions, emptyQuestion()] } : s
    ));
  }

  function removeQuestion(si, qi) {
    setSurveys(prev => prev.map((s, i) =>
      i === si ? { ...s, questions: s.questions.filter((_, j) => j !== qi) } : s
    ));
  }

  function updateQuestion(si, qi, field, value) {
    setSurveys(prev => prev.map((s, i) =>
      i === si ? {
        ...s,
        questions: s.questions.map((q, j) => j === qi ? { ...q, [field]: value } : q)
      } : s
    ));
  }

  // ── Submit ───────────────────────────────────────────────────────────────

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const user = getUser();
      const payload = {
        name: studyName,
        researcherId: user.researcherId,
        surveys: surveys.map(s => ({
          name: s.name,
          scheduleType: s.scheduleType,
          questions: s.questions.map(q => ({
            text: q.text,
            type: q.type
          }))
        }))
      };
      const created = await createFullStudy(payload);
      router.push(`/studies/${created.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-rose-950 text-rose-50 font-sans">
      <NavBar />

      <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-white">Create New Study</h1>
          <p className="mt-1 text-rose-300 text-sm">Define your study, surveys, and questions in one go.</p>
        </div>

        {error && (
          <div className="rounded-xl bg-red-500/10 border border-red-500/20 px-4 py-3 text-sm text-red-300 mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Study Name */}
          <div className="rounded-2xl border border-rose-800/40 bg-white/5 p-6">
            <label className="block text-sm font-medium text-rose-200 mb-2">Study Name</label>
            <input
              type="text"
              required
              value={studyName}
              onChange={e => setStudyName(e.target.value)}
              placeholder="e.g. Sleep & Mood Study"
              className="block w-full rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 placeholder:text-rose-300/50 outline-none transition-all sm:text-sm"
            />
          </div>

          {/* Surveys */}
          {surveys.map((survey, si) => (
            <div key={si} className="rounded-2xl border border-rose-800/40 bg-white/5 p-6 space-y-5">
              <div className="flex items-center justify-between">
                <h2 className="text-base font-semibold text-white">Survey {si + 1}</h2>
                {surveys.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeSurvey(si)}
                    className="text-xs text-rose-400 hover:text-red-400 transition-colors"
                  >
                    Remove survey
                  </button>
                )}
              </div>

              {/* Survey Name */}
              <div>
                <label className="block text-sm font-medium text-rose-200 mb-2">Survey Name</label>
                <input
                  type="text"
                  required
                  value={survey.name}
                  onChange={e => updateSurvey(si, 'name', e.target.value)}
                  placeholder="e.g. Morning Check-in"
                  className="block w-full rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 placeholder:text-rose-300/50 outline-none transition-all sm:text-sm"
                />
              </div>

              {/* Schedule Type */}
              <div>
                <label className="block text-sm font-medium text-rose-200 mb-2">Schedule</label>
                <div className="flex gap-2 flex-wrap">
                  {SCHEDULE_TYPES.map(type => (
                    <button
                      key={type}
                      type="button"
                      onClick={() => updateSurvey(si, 'scheduleType', type)}
                      className={`rounded-xl px-4 py-2 text-xs font-medium ring-1 transition-all ${
                        survey.scheduleType === type
                          ? 'bg-rose-600 text-white ring-rose-500'
                          : 'bg-white/5 text-rose-300 ring-rose-300/20 hover:bg-white/10'
                      }`}
                    >
                      {type}
                    </button>
                  ))}
                </div>
              </div>

              {/* Questions */}
              <div className="space-y-3">
                <label className="block text-sm font-medium text-rose-200">Questions</label>
                {survey.questions.map((question, qi) => (
                  <div key={qi} className="flex gap-3 items-start">
                    <div className="flex-1 flex gap-3">
                      <input
                        type="text"
                        required
                        value={question.text}
                        onChange={e => updateQuestion(si, qi, 'text', e.target.value)}
                        placeholder={`Question ${qi + 1}`}
                        className="flex-1 rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 placeholder:text-rose-300/50 outline-none transition-all sm:text-sm"
                      />
                      <select
                        value={question.type}
                        onChange={e => updateQuestion(si, qi, 'type', e.target.value)}
                        className="rounded-xl border-0 py-2.5 px-3 bg-rose-900/50 text-rose-200 ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 outline-none transition-all sm:text-sm"
                      >
                        {QUESTION_TYPES.map(t => (
                          <option key={t} value={t}>{t}</option>
                        ))}
                      </select>
                    </div>
                    {survey.questions.length > 1 && (
                      <button
                        type="button"
                        onClick={() => removeQuestion(si, qi)}
                        className="mt-2.5 text-rose-500 hover:text-red-400 text-lg leading-none transition-colors"
                      >
                        ×
                      </button>
                    )}
                  </div>
                ))}
                <button
                  type="button"
                  onClick={() => addQuestion(si)}
                  className="text-xs text-rose-400 hover:text-rose-200 transition-colors"
                >
                  + Add question
                </button>
              </div>
            </div>
          ))}

          {/* Add Survey */}
          <button
            type="button"
            onClick={addSurvey}
            className="w-full rounded-2xl border border-dashed border-rose-700/50 py-4 text-sm text-rose-400 hover:border-rose-500 hover:text-rose-200 transition-all"
          >
            + Add another survey
          </button>

          {/* Submit */}
          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={() => router.back()}
              className="flex-1 rounded-xl bg-white/5 px-4 py-3 text-sm font-medium text-rose-200 ring-1 ring-inset ring-rose-300/20 hover:bg-white/10 transition-all"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 flex justify-center rounded-xl bg-gradient-to-r from-rose-500 to-rose-600 px-4 py-3 text-sm font-semibold text-white hover:from-rose-400 hover:to-rose-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              ) : 'Create Study'}
            </button>
          </div>
        </form>
      </main>
    </div>
  );
}
