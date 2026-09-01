# Kinetiq

**AI-powered career progress tracker with a custom time-decay momentum scoring engine and async LLM-based weekly recommendations.**

[![Live Backend](https://img.shields.io/badge/backend-live-brightgreen)](https://kinetiq-backend-qq54.onrender.com)
[![Live App](https://img.shields.io/badge/app-live-brightgreen)](https://kinetiq-frontend.vercel.app)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](#license)

**Live app:** [kinetiq-frontend.vercel.app](https://kinetiq-frontend.vercel.app)
**Live backend:** [kinetiq-backend-qq54.onrender.com](https://kinetiq-backend-qq54.onrender.com)
*Both are hosted on free-tier infrastructure — the backend may take up to a minute to respond after a period of inactivity while it spins back up.*

---

## Overview

Most "career tracker" apps are glorified checklists — check a box, feel good, no real signal on whether you're actually improving. Kinetiq is built around one question: **is this action backed by real evidence, and is it moving the needle over time?**

It turns daily career-building actions (DSA practice, project work, learning) into a measurable, evidence-verified progress score — combining a custom time-decay scoring algorithm with AI-generated weekly recommendations, instead of just tracking a to-do list.

**Status:** Fully deployed and functional, frontend and backend. Auth, check-in submission with GitHub-verified evidence, the momentum engine, and AI-generated weekly recommendations are all live and working end-to-end.

## Core design decisions

- **Momentum score, not a streak counter.** Daily progress is calculated with a weighted formula that includes exponential decay — consistent effort compounds, missed days meaningfully reduce momentum, and every day's score is preserved as an immutable snapshot (not overwritten), enabling a true progress-over-time view.
- **Evidence verification, not self-reported trust.** Check-ins tied to a GitHub link are validated against the GitHub API — commit exists, falls within the check-in window — rather than blindly trusting a pasted URL.
- **Stateless JWT authentication.** No server-side sessions — every request is authenticated independently via a signed token. A custom `OncePerRequestFilter` validates the token and populates Spring Security's context before any controller runs.
- **Environment-based configuration.** Local development reads from a gitignored properties file; production (Render) reads the same configuration keys from environment variables. No secrets are ever committed to version control.
- **AI recommendations run asynchronously.** LLM calls (via LangChain4j + Gemini) never block a request thread. Responses are validated as structured JSON, with a safe fallback if the model returns malformed output.
- **CORS scoped explicitly**, not wildcarded — only the local dev server and the live Vercel frontend are permitted origins.

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA, Hibernate |
| Auth | JWT (jjwt), custom authentication filter, BCrypt password hashing |
| Database | MySQL (Aiven, cloud-hosted) |
| AI | LangChain4j, Google Gemini API |
| Frontend | React, Vite, Tailwind CSS v4 — see [kinetiq-frontend](https://github.com/sulekhathakur/kinetiq-frontend) |
| Deployment | Render (backend, Docker), Aiven (database), Vercel (frontend) |

## Architecture

```
React Frontend (Vercel)
        │
        ▼
    REST APIs
        │
        ▼
JWT Auth Filter  ──►  Spring Boot Backend (Render, Docker)  ──►  MySQL (Aiven)
                              │
                              └──►  AI Layer (LangChain4j + Groq)  [planned]
```


**Request flow:** Client → JWT filter (validates token, sets authenticated identity) → Controller → Service → Repository → Database, with DTOs at the boundary so internal entities (and fields like password hashes) are never exposed over the wire.

## Entity design

Five core tables model one continuous feedback loop: submit a check-in → attach evidence → recompute momentum → receive AI-generated guidance → repeat.

| Table | Purpose |
|---|---|
| `users` | Account and credentials (hashed) |
| `check_ins` | Daily logged actions (DSA / project / learning) |
| `evidence` | Proof attached to a check-in (link, screenshot, text) |
| `momentum_snapshots` | Immutable daily momentum score, enabling trend charts |
| `weekly_recommendations` | AI-generated guidance based on recent activity |

## API endpoints

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Create a new account, returns a JWT | No |
| `POST` | `/api/auth/login` | Authenticate, returns a JWT | No |
| `POST` | `/api/checkins` | Submit a daily check-in (triggers momentum recompute) | Yes (Bearer JWT) |
| `POST` | `/api/evidence` | Attach evidence to a check-in; GitHub commit links are automatically verified against the GitHub API | Yes (Bearer JWT) |
| `GET` | `/api/momentum/latest` | Retrieve the user's most recent momentum snapshot | Yes (Bearer JWT) |
| `GET` | `/api/recommendations/generate` | Generate a new AI weekly recommendation (async Gemini call) | Yes (Bearer JWT) |
| `GET` | `/api/recommendations/latest` | Retrieve the most recently saved recommendation | Yes (Bearer JWT) |

## Local setup

1. Clone the repo.
2. Create `src/main/resources/application-local.properties` with your own values:
```properties
   spring.datasource.url=jdbc:mysql://<host>:<port>/<database>?ssl-mode=REQUIRED
   spring.datasource.username=<your-db-username>
   spring.datasource.password=<your-db-password>
   spring.jpa.hibernate.ddl-auto=update
   jwt.secret=<your-random-secret-key>
   jwt.expiration=86400000
   gemini.api.key=<your-gemini-api-key>
   gemini.model=gemini-3.5-flash-lite
```
   This file is gitignored and never committed.
3. Run:
```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```
   (Quotes required on Windows/PowerShell. The `local` profile is activated explicitly here since production reads configuration from environment variables instead.)

## Roadmap

- [x] Project setup, entity design, database connectivity
- [x] All entities + repositories
- [x] JWT authentication — register/login, password hashing, global exception handling
- [x] Backend deployed to Render (Docker), verified live end-to-end
- [x] JWT authentication filter validating protected routes
- [x] Check-in submission endpoint (verified end-to-end)
- [x] Evidence submission endpoint with ownership validation (verified end-to-end)
- [x] Momentum scoring engine with time-decay formula (unit tested, verified end-to-end)
- [x] Evidence verification via GitHub API (verified end-to-end)
- [x] AI weekly recommendation engine (LangChain4j + Gemini, async, structured JSON validation with fallback)
- [x] React frontend, deployed to Vercel, connected to live backend

## Author

**Sulekha Thakur** — [GitHub](https://github.com/sulekhathakur)
Java · Spring Boot · MySQL · JWT Auth · Docker · Render · React · Vercel

## License

MIT