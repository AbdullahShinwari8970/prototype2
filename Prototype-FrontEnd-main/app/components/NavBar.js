"use client";

import { getUser, logout } from '../lib/auth';
import Link from 'next/link';

export default function NavBar() {
  const user = getUser();

  return (
    <nav className="border-b border-rose-800/50 bg-rose-950/80 backdrop-blur-xl sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">
        <div className="flex items-center gap-6">
          <Link href="/" className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-rose-600 flex items-center justify-center font-bold text-white shadow-lg shadow-rose-600/30">P</div>
            <span className="text-xl font-bold tracking-tight text-white">Prototype</span>
          </Link>
          <Link href="/" className="text-sm text-rose-300 hover:text-white transition-colors">
            Studies
          </Link>
        </div>
        <div className="flex items-center gap-4">
          <span className="text-sm text-rose-200">{user?.name}</span>
          <button
            onClick={logout}
            className="rounded-xl bg-white/5 px-4 py-2 text-sm font-medium text-rose-200 ring-1 ring-inset ring-rose-300/20 hover:bg-white/10 hover:text-white transition-all"
          >
            Sign out
          </button>
        </div>
      </div>
    </nav>
  );
}
