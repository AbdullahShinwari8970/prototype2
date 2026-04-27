## Tech Stack

- **Backend:** Spring Boot 
- **Frontend:** Next.js (React)
- **Database:** PostgreSQL 16
- **Containerisation:** Docker and Docker Compose

---

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running

---

## Setup and Running

### 1. Clone or extract the project

### 2. Set up environment variables

.env is already setup with credidentials.
Though you can use your own by following the instructions below.

Open `.env` and fill in:
- `POSTGRES_PASSWORD` — any password you choose
- `PGPASSWORD` — same as above
- `JWT_SECRET` — any long random string (min 32 characters)
- `MAIL_USERNAME` — your Gmail address
- `MAIL_PASSWORD` — your Gmail App Password

> **How to generate a Gmail App Password:**  
> Google Account → Security → 2-Step Verification → App Passwords → Generate

### 3. Start the full tech stack

```bash
docker-compose up --build
```

This starts three containers:
- `prototype2-db` -> PostgreSQL on port 5433
- `prototype2-app` -> Spring Boot API on port 8080
- `prototype2-frontend` —> Next.js on port 3000

### 4. Open the app

Visit: [http://localhost:3000](http://localhost:3000)

---

## Demo Setup (Recommended)

A demo seed dataset is included to populate the system with realistic data for testing.

### Step 1: Create a researcher account

Go to [http://localhost:3000/signup](http://localhost:3000/signup) and sign up with:
- **Email:** `demo@research.com`
- **Password:** `Demo1234!`

### Step 2: Run the seed SQL

Connect to the database and run `demo_seed.sql`:

```bash
docker exec -i prototype2-db psql -U admin -d prototype2 < demo_seed.sql
```

This inserts:
- 1 study: **Physical Activity Tracking Study**
- 2 surveys: Baseline (one-time) + Daily Activity Log (daily)
- 3 participants: Alice Murphy, Ben Clarke, Cara Nolan
- Survey tokens and responses across 3 days

### Step 3: Log in

Use `demo@research.com / Demo1234!` to log in at [http://localhost:3000/login](http://localhost:3000/login)

---

## API Documentation

Swagger UI is available at:  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Stopping the App

```bash
docker-compose down
```

To also remove the database volume (full reset):

```bash
docker-compose down -v
```
