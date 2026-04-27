const API_BASE = process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8080";


/**
 * Register a new researcher account.
 * @param {string} name
 * @param {string} email
 * @param {string} password
 * @returns {Promise<{message: string}>}
 */
export async function signup(name, email, password) {
  const res = await fetch(`${API_BASE}/api/auth/signup`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password }),
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data.error || "Signup failed. Please try again.");
  }

  return data;
}

/**
 * Login researcher account.
 * @param {string} email
 * @param {string} password
 * @returns {Promise<{token: string, researcherId: number, email: string, name: string}>}
 */
export async function login(email, password) {
  const res = await fetch(`${API_BASE}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data.error || "Invalid email or password.");
  }

  //Persist auth state
  localStorage.setItem("token", data.token);
  localStorage.setItem(
    "user",
    JSON.stringify({
      researcherId: data.researcherId,
      email: data.email,
      name: data.name,
    })
  );

  return data;
}

export function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  window.location.href = "/login";
}

//jwt token
export function getToken() {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("token");
}

//returns {{researcherId: number, email: string, name: string}|null}

export function getUser() {
  if (typeof window === "undefined") return null;
  const raw = localStorage.getItem("user");
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}


export function isAuthenticated() {
  return !!getToken();
}
