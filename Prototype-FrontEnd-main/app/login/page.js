"use client";

import React, { useState } from 'react';
import { MdEmail } from "react-icons/md";
import { FaLock } from "react-icons/fa";
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { login } from '../lib/auth';

export default function LoginPage() {
    const router = useRouter();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await login(email, password);
            router.push('/');
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="min-h-screen bg-rose-950 flex isolate relative overflow-hidden text-rose-50 font-sans">
            <div className="absolute top-0 left-1/2 -z-10 -translate-x-1/2 blur-3xl xl:-top-6 rounded-full opacity-30" aria-hidden="true">
                <div className="aspect-[1155/678] w-[72.1875rem] bg-gradient-to-tr from-[#ff80b5] to-[#9089fc] opacity-40"></div>
            </div>

            <div className="flex flex-1 flex-col justify-center px-4 py-12 sm:px-6 lg:flex-none lg:px-20 xl:px-24 relative z-10 w-full lg:w-1/2">
                <div className="mx-auto w-full max-w-sm lg:w-96">
                    <div>
                        <div className="flex items-center gap-2">
                            <div className="w-8 h-8 rounded-lg bg-rose-600 flex items-center justify-center font-bold text-white shadow-lg shadow-rose-600/30">P</div>
                            <span className="text-xl font-bold tracking-tight text-white">Prototype</span>
                        </div>
                        <h2 className="mt-8 text-3xl font-bold tracking-tight text-white">
                            Sign in to your account
                        </h2>
                        <p className="mt-2 text-sm leading-6 text-rose-200">
                            Don't have an account?{' '}
                            <Link href="/signup" className="font-semibold text-rose-400 hover:text-rose-300 transition-colors">
                                Sign up here
                            </Link>
                        </p>
                    </div>

                    <div className="mt-5">
                        <div>
                            <form onSubmit={handleSubmit} className="space-y-6">
                                {error && (
                                    <div className="rounded-xl bg-red-500/10 border border-red-500/20 px-4 py-3 text-sm text-red-300">
                                        {error}
                                    </div>
                                )}

                                <div className="flex items-center gap-2">
                                    <label htmlFor="email" className="block text-sm font-medium leading-6 text-rose-100">
                                        <MdEmail />
                                    </label>
                                    <div className="mt-2 w-full">
                                        <input
                                            id="email"
                                            name="email"
                                            type="email"
                                            autoComplete="email"
                                            required
                                            value={email}
                                            onChange={(e) => setEmail(e.target.value)}
                                            className="block w-full rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white shadow-sm ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-inset focus:ring-rose-400 sm:text-sm sm:leading-6 placeholder:text-rose-300/50 transition-all outline-none"
                                            placeholder="john@example.com"
                                        />
                                    </div>
                                </div>

                                <div className="flex items-center gap-2">
                                    <div className="flex items-center justify-between">
                                        <label htmlFor="password" className="block text-sm font-medium leading-6 text-rose-100">
                                            <FaLock />
                                        </label>
                                    </div>
                                    <div className="mt-2 w-full">
                                        <input
                                            id="password"
                                            name="password"
                                            type="password"
                                            autoComplete="current-password"
                                            required
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            className="block w-full rounded-xl border-0 py-2.5 px-3 bg-white/5 text-white shadow-sm ring-1 ring-inset ring-rose-300/20 focus:ring-2 focus:ring-inset focus:ring-rose-400 sm:text-sm sm:leading-6 placeholder:text-rose-300/50 transition-all outline-none"
                                            placeholder="••••••••"
                                        />
                                    </div>
                                </div>

                                <div className="text-sm w-full flex justify-end">
                                    <a href="#" className="font-semibold text-rose-400 hover:text-rose-300">
                                        Forgot password?
                                    </a>
                                </div>

                                <div>
                                    <button
                                        type="submit"
                                        disabled={loading}
                                        className="flex w-full justify-center rounded-xl bg-gradient-to-r from-rose-500 to-rose-600 px-3 py-3 text-sm font-semibold text-white shadow-md shadow-rose-900/20 hover:from-rose-400 hover:to-rose-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-rose-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                                    >
                                        {loading ? (
                                            <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                            </svg>
                                        ) : (
                                            'Sign in'
                                        )}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            {/* Right Side Image/Graphic */}
            <div className="relative hidden w-0 flex-1 lg:block">
                <div className="absolute inset-0 bg-gradient-to-br from-rose-900 to-rose-950 flex flex-col items-center justify-center p-12 overflow-hidden">
                    {/* Abstract background shapes */}
                    <div className="absolute top-1/4 right-1/4 w-96 h-96 bg-rose-600 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-[pulse_8s_ease-in-out_infinite]"></div>
                    <div className="absolute top-1/3 left-1/4 w-96 h-96 bg-purple-700 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-[pulse_10s_ease-in-out_infinite] delay-1000"></div>
                    <div className="absolute -bottom-8 left-1/2 w-96 h-96 bg-pink-700 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-[pulse_9s_ease-in-out_infinite] delay-700"></div>

                    <div className="relative z-10 w-full max-w-lg bg-white/5 backdrop-blur-2xl rounded-3xl border border-white/10 p-12 shadow-2xl">
                        <h3 className="text-3xl font-bold text-white mb-6">Discover Longitudinal Data Collection System for Researchers. </h3>
                        <p className="text-rose-200 text-lg leading-relaxed">
                            A Longitudinal Data Collection System for Researchers. Reaschers are able to easily design a quetionaire/form and schedule it to be sent to participants through tockenized email link for submission..
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}
