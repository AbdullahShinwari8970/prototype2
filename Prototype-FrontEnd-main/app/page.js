"use client";

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { isAuthenticated, getUser } from './lib/auth';
import { getStudies } from './lib/api';
import NavBar from './components/NavBar';

export default function Dashboard() {
  const router = useRouter();
  const [studies, setStudies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAuthenticated()) {
      router.replace('/login');
      return;
    }
    const user = getUser();
    getStudies(user.researcherId)
      .then(setStudies)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, [router]);

  if (loading) {
    return (
      <div className="min-h-screen bg-rose-950 flex items-center justify-center">
        <svg className="animate-spin h-8 w-8 text-rose-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-rose-950 text-rose-50 font-sans">
      <NavBar />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold text-white">Your Studies</h1>
            <p className="mt-1 text-rose-300 text-sm">{studies.length} study{studies.length !== 1 ? 's' : ''} found</p>
          </div>
          <Link
            href="/studies/create"
            className="rounded-xl bg-gradient-to-r from-rose-500 to-rose-600 px-5 py-2.5 text-sm font-semibold text-white shadow-md hover:from-rose-400 hover:to-rose-500 transition-all"
          >
            + New Study
          </Link>
        </div>

        {error && (
          <div className="rounded-xl bg-red-500/10 border border-red-500/20 px-4 py-3 text-sm text-red-300 mb-6">
            {error}
          </div>
        )}

        {/* Studies Grid */}
        {studies.length === 0 ? (
          <div className="text-center py-24 rounded-2xl border border-rose-800/40 bg-white/5">
            <p className="text-rose-300 text-lg">No studies yet.</p>
            <p className="text-rose-400 text-sm mt-1">Create your first study to get started.</p>
            <Link
              href="/studies/create"
              className="inline-block mt-6 rounded-xl bg-gradient-to-r from-rose-500 to-rose-600 px-5 py-2.5 text-sm font-semibold text-white hover:from-rose-400 hover:to-rose-500 transition-all"
            >
              + New Study
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {studies.map(study => (
              <Link
                key={study.id}
                href={`/studies/${study.id}`}
                className="rounded-2xl border border-rose-800/40 bg-white/5 p-6 hover:bg-white/10 hover:border-rose-600/50 transition-all group"
              >
                <div className="flex items-start justify-between">
                  <div className="w-10 h-10 rounded-xl bg-rose-600/20 border border-rose-600/30 flex items-center justify-center text-rose-400 font-bold text-lg">
                    {study.name.charAt(0).toUpperCase()}
                  </div>
                  <span className="text-xs text-rose-400 bg-rose-400/10 rounded-full px-2 py-1">
                    {study.surveys?.length || 0} survey{study.surveys?.length !== 1 ? 's' : ''}
                  </span>
                </div>
                <h2 className="mt-4 text-lg font-semibold text-white group-hover:text-rose-200 transition-colors">
                  {study.name}
                </h2>
                <p className="mt-1 text-sm text-rose-400">
                  View details →
                </p>
              </Link>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
