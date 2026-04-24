"use client";

import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';

const API_BASE = process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8080";

export default function SurveyPage() {
  const { token } = useParams();

  const [survey, setSurvey] = useState(null);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    fetch(`${API_BASE}/api/participant/survey/${token}`)
      .then(res => res.json())
      .then(data => {
        if (data.error) throw new Error(data.error);
        setSurvey(data);
        // Initialise answers map with empty strings keyed by question ID
        const initial = {};
        data.questions.forEach(q => { initial[q.id] = ''; });
        setAnswers(initial);
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, [token]);

  function handleChange(questionId, value) {
    setAnswers(prev => ({ ...prev, [questionId]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      const payload = {
        answers: Object.entries(answers).map(([questionId, answerValue]) => ({
          questionId: parseInt(questionId),
          answerValue
        }))
      };

      const res = await fetch(`${API_BASE}/api/participant/survey/${token}/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await res.json();
      if (!res.ok) throw new Error(data.error || 'Submission failed');

      setSubmitted(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  // ── States ────────────────────────────────────────────────────────────────

  if (loading) return (
    <div className="min-h-screen bg-rose-950 flex items-center justify-center">
      <svg className="animate-spin h-8 w-8 text-rose-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
    </div>
  );

  if (error) return (
    <div className="min-h-screen bg-rose-950 flex items-center justify-center text-rose-50 font-sans px-4">
      <div className="max-w-md w-full text-center">
        <div className="w-16 h-16 rounded-2xl bg-red-500/10 border border-red-500/20 flex items-center justify-center mx-auto mb-6">
          <span className="text-2xl">⚠️</span>
        </div>
        <h1 className="text-2xl font-bold text-white mb-2">Survey Unavailable</h1>
        <p className="text-red-300 text-sm">{error}</p>
        <p className="text-rose-400 text-xs mt-4">
          This link may have expired, already been used, or is invalid.
        </p>
      </div>
    </div>
  );

  if (submitted) return (
    <div className="min-h-screen bg-rose-950 flex items-center justify-center text-rose-50 font-sans px-4">
      <div className="max-w-md w-full text-center">
        <div className="w-16 h-16 rounded-2xl bg-green-500/10 border border-green-500/20 flex items-center justify-center mx-auto mb-6">
          <span className="text-3xl">✓</span>
        </div>
        <h1 className="text-2xl font-bold text-white mb-2">Thank You!</h1>
        <p className="text-rose-200 text-sm">
          Your responses have been recorded. You may now close this page.
        </p>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-rose-950 text-rose-50 font-sans">
      {/* Header */}
      <div className="border-b border-rose-800/50 bg-rose-950/80 backdrop-blur-xl">
        <div className="max-w-2xl mx-auto px-4 h-16 flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-rose-600 flex items-center justify-center font-bold text-white shadow-lg shadow-rose-600/30">P</div>
          <span className="text-lg font-bold tracking-tight text-white">Prototype</span>
        </div>
      </div>

      {/* Survey Form */}
      <main className="max-w-2xl mx-auto px-4 py-12">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-white">{survey.surveyName}</h1>
          <p className="mt-1 text-rose-300 text-sm">
            Please answer all {survey.questions.length} question{survey.questions.length !== 1 ? 's' : ''} below.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {survey.questions.map((question, idx) => (
            <div key={question.id} className="rounded-2xl border border-rose-800/40 bg-white/5 p-6">
              <label className="block text-sm font-medium text-rose-100 mb-3">
                <span className="text-rose-400 mr-2">{idx + 1}.</span>
                {question.text}
                <span className="ml-2 text-xs text-rose-500">[{question.type}]</span>
              </label>

              <textarea
                  required
                  rows={3}
                  value={answers[question.id] || ''}
                  onChange={e => handleChange(question.id, e.target.value)}
                  placeholder="Type your answer here..."
                  className="block w-full rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-rose-400 placeholder:text-rose-300/50 outline-none transition-all sm:text-sm resize-none"
                />
            </div>
          ))}

          {error && (
            <p className="text-sm text-red-300">{error}</p>
          )}

          <button
            type="submit"
            disabled={submitting}
            className="w-full flex justify-center rounded-xl bg-gradient-to-r from-rose-500 to-rose-600 px-4 py-3 text-sm font-semibold text-white hover:from-rose-400 hover:to-rose-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {submitting ? (
              <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            ) : 'Submit Responses'}
          </button>
        </form>
      </main>
    </div>
  );
}
