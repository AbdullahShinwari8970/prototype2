"use client";

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { isAuthenticated, getUser, logout } from './lib/auth';

export default function Home() {
    const router = useRouter();
    const [user, setUser] = useState(null);
    const [checking, setChecking] = useState(true);

    useEffect(() => {
        if (!isAuthenticated()) {
            router.replace('/login');
            return;
        }
        setUser(getUser());
        setChecking(false);
    }, [router]);

    if (checking) {
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
            {/* Top Navigation Bar */}
            <nav className="border-b border-rose-800/50 bg-rose-950/80 backdrop-blur-xl">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">
                    <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-lg bg-rose-600 flex items-center justify-center font-bold text-white shadow-lg shadow-rose-600/30">P</div>
                        <span className="text-xl font-bold tracking-tight text-white">Prototype</span>
                    </div>
                    <div className="flex items-center gap-4">
                        <span className="text-sm text-rose-200">
                            {user?.name}
                        </span>
                        <button
                            onClick={logout}
                            className="rounded-xl bg-white/5 px-4 py-2 text-sm font-medium text-rose-200 ring-1 ring-inset ring-rose-300/20 hover:bg-white/10 hover:text-white transition-all"
                        >
                            Sign out
                        </button>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
                <div className="text-center">
                    <h1 className="text-4xl font-bold tracking-tight text-white sm:text-5xl">
                        Welcome back, <span className="text-rose-400">{user?.name}</span>
                    </h1>
                    <p className="mt-4 text-lg text-rose-200 max-w-2xl mx-auto">
                        You are logged in as <span className="font-medium text-white">{user?.email}</span>. Your dashboard is ready.
                    </p>
                </div>
            </main>
        </div>
    );
}