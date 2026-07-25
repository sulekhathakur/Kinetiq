# Kinetiq — AI-Powered Career Progress Tracker

Kinetiq is a backend system that turns daily career-building actions (DSA practice, project work, learning) into a measurable, evidence-verified progress score. It combines a custom time-decay scoring algorithm with AI-generated weekly recommendations to help users stay consistent and improve deliberately, instead of just tracking a to-do list.

**Status:** In active development (Day 1 of 30). Core entities and database connectivity are live; API endpoints, scoring engine, and AI layer are in progress.

## Why this project

Most "career tracker" apps are glorified checklists — check a box, feel good, no real signal on whether you're actually improving. Kinetiq is built around one question: **is this action backed by real evidence, and is it moving the needle over time?**

## Core design decisions

- **Momentum score, not a streak counter.** Daily progress is calculated with a weighted formula that includes exponential decay — consistent effort compounds, missed days meaningfully reduce momentum, and every day's score is preserved as an immutable snapshot (not overwritten), enabling a true progress-over-time view.
- **Evidence verification, not self-reported trust.** Check-ins tied to a GitHub link are validated against the GitHub API (commit exists, falls in the check-in window, is a non-trivial change) rather than blindly trusting a pasted URL.
- **AI recommendations run asynchronously.** LLM calls (via LangChain4j + Groq) never block a request thread, and responses are validated as structured JSON with fallback handling if the model returns malformed output.

## Tech stack

**Backend:** Java 17, Spring Boot, Spring Security (JWT), Spring Data JPA, Hibernate
**Database:** MySQL (Aiven, cloud-hosted)
**AI:** LangChain4j, Groq API
**Frontend (planned):** React, Vite, Tailwind CSS
**Deployment (planned):** Render (backend), Vercel (frontend)

## Architecture
```
React Frontend
│
▼
REST APIs
│
▼
Spring Boot Backend
│
├── MySQL (Aiven)
├── Cloudinary (evidence storage)
└── AI Layer (LangChain4j + Groq)
```

## Entity design

Five core tables: `users`, `check_ins`, `evidence`, `momentum_snapshots`, `weekly_recommendations` — modeling one continuous feedback loop: submit a check-in → attach evidence → recompute momentum → receive AI-generated guidance → repeat.

## Local setup

1. Clone the repo
2. Create `src/main/resources/application-local.properties` with the following keys, using your own MySQL and JWT values:
```properties
   spring.datasource.url=jdbc:mysql://<host>:<port>/<database>?ssl-mode=REQUIRED
   spring.datasource.username=<your-db-username>
   spring.datasource.password=<your-db-password>
   spring.jpa.hibernate.ddl-auto=update
   jwt.secret=<your-random-secret-key>
   jwt.expiration=86400000
```
   This file is gitignored and never committed.
3. Run `mvn spring-boot:run "-Dspring-boot.run.profiles=local"` (quotes required on Windows/PowerShell — the local profile is activated explicitly since production config comes from environment variables instead)

## Roadmap

- [x] Project setup, entity design, database connectivity
- [x] All entities (User, CheckIn, Evidence, MomentumSnapshot) + repositories
- [x] Auth DTOs + password encoder configuration
- [x] JWT authentication (token generation, register/login endpoints)
- [x] Global exception handling for validation and auth errors
- [ ] Core CRUD APIs (check-ins, evidence)
- [ ] Momentum scoring engine + evidence verification
- [ ] AI weekly recommendation engine
- [ ] React frontend + deployment
---

*Built by Sulekha Thakur as a demonstration of backend architecture, AI integration, and product engineering — not a course project.*