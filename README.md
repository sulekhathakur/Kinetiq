# Kinetiq

**AI-powered career progress tracker with a custom time-decay momentum scoring engine and async LLM-based weekly recommendations.**

[![Live Backend](https://img.shields.io/badge/backend-live-brightgreen)](https://kinetiq-backend-qq54.onrender.com)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](#license)

**Live backend:** [kinetiq-backend-qq54.onrender.com](https://kinetiq-backend-qq54.onrender.com)
*Hosted on free-tier infrastructure — the first request after a period of inactivity may take up to a minute while the service spins back up.*

---

## Overview

Most "career tracker" apps are glorified checklists — check a box, feel good, no real signal on whether you're actually improving. Kinetiq is built around one question: **is this action backed by real evidence, and is it moving the needle over time?**

It turns daily career-building actions (DSA practice, project work, learning) into a measurable, evidence-verified progress score — combining a custom time-decay scoring algorithm with AI-generated weekly recommendations, instead of just tracking a to-do list.

**Status:** Backend in active development. Auth, entity layer, deployment, and the first authenticated feature (check-in submission) are live and verified end-to-end. Evidence submission, the momentum engine, and the AI recommendation layer are in progress.

## Core design decisions

- **Momentum score, not a streak counter.** Daily progress is calculated with a weighted formula that includes exponential decay — consistent effort compounds, missed days meaningfully reduce momentum, and every day's score is preserved as an immutable snapshot (not overwritten), enabling a true progress-over-time view.
- **Evidence verification, not self-reported trust.** Check-ins tied to a GitHub link will be validated against the GitHub API (commit exists, falls in the check-in window, is a non-trivial change) rather than blindly trusting a pasted URL.
- **Stateless JWT authentication.** No server-side sessions — every request is authenticated independently via a signed token, verified against a secret key rather than a database lookup. A custom `OncePerRequestFilter` validates the token and populates Spring Security's context before any controller runs.
- **Environment-based configuration.** Local development reads from a gitignored properties file; production (Render) reads the same configuration keys from environment variables. No secrets are ever committed to version control.
- **AI recommendations will run asynchronously.** LLM calls (via LangChain4j + Groq) are designed to never block a request thread, with responses validated as structured JSON and fallback handling for malformed output.

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA, Hibernate |
| Auth | JWT (jjwt), custom authentication filter, BCrypt password hashing |
| Database | MySQL (Aiven, cloud-hosted) |
| AI *(planned)* | LangChain4j,  Google Gemini API |
| Frontend *(planned)* | React, Vite, Tailwind CSS |
| Deployment | Render (backend, Docker), Aiven (database), Vercel (frontend, planned) |

## Architecture

```
React Frontend (planned)
        │
        ▼
    REST APIs
        │
        ▼
JWT Auth Filter  ──►  Spring Boot Backend  ──►  MySQL (Aiven)
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
| `weekly_recommendations` | AI-generated guidance based on recent activity *(planned)* |

## API endpoints

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Create a new account, returns a JWT | No |
| `POST` | `/api/auth/login` | Authenticate, returns a JWT | No |
| `POST` | `/api/checkins` | Submit a daily check-in (triggers momentum recompute) | Yes (Bearer JWT) |
| `POST` | `/api/evidence` | Attach evidence to a check-in; GitHub commit links are automatically verified against the GitHub API | Yes (Bearer JWT) |
| `GET` | `/api/momentum/latest` | Retrieve the user's most recent momentum snapshot | Yes (Bearer JWT) |   
More endpoints (momentum, weekly recommendations) are in progress — see [Roadmap](#roadmap).

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
   ```
   This file is gitignored and never committed.
3. Run:
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=local"
   ```
   (Quotes required on Windows/PowerShell. The `local` profile is activated explicitly here since production reads configuration from environment variables instead.)

## Roadmap

- [x] Project setup, entity design, database connectivity
- [x] All entities (`User`, `CheckIn`, `Evidence`, `MomentumSnapshot`) + repositories
- [x] JWT authentication — register/login endpoints, password hashing, global exception handling
- [x] Backend deployed to Render (Docker), verified live end-to-end
- [x] JWT authentication filter validating protected routes
- [x] Check-in submission endpoint (authenticated, verified end-to-end)
- [x] Evidence submission endpoint with ownership validation (verified end-to-end)
- [x] Momentum scoring engine with time-decay formula (unit tested, auto-recomputes on check-in, verified end-to-end)
- [x] Evidence verification via GitHub API (verified end-to-end, both match and mismatch cases)
- [x] AI weekly recommendation engine (LangChain4j + Gemini, async, structured JSON validation with fallback, verified end-to-end)
- [ ] React frontend + deployment

## Author

**Sulekha Thakur** — [GitHub](https://github.com/sulekhathakur)
Java · Spring Boot · MySQL · JWT Auth · Docker · Render

## License

MIT