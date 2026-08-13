# NomadCompass – Product Requirements Document (PRD)

## 1. Executive Summary & Vision

NomadCompass is an **offline‑first mobile reference utility** for digital nomads, remote workers, and international travelers. It aggregates static country profiles, real‑time climate data, safety advisories, holiday calendars, and context‑aware conversion tools into a unified experience that works **100 % offline**.

- **Instant Utility** – Core data and saved information are always available without cellular or Wi‑Fi.
- **Zero‑Subscription / Keyless Architecture** – Uses free, open‑access public APIs; no user authentication keys, paid tiers, or third‑party tracking.
- **Contextual Intelligence** – Auto‑calculates time differences, currency exchanges, packing lists, and weather suitability according to the user’s home country and preferred units.

## 2. Target Audience & Personas

| Audience | Needs | Pain Points |
|---|---|---|
| **Digital Nomads & Remote Workers** | Quick insight into working‑friendly weather, time‑zone overlap, banking & coworking availability. | Subscription travel apps fail when connectivity drops at border checkpoints. |
| **Low‑Connectivity Travelers** | Reliable currency conversion, safety advisories before crossing borders. | Cluttered apps require login and bombard users with ads. |

### Personas
- **Sarah (Remote Freelancer)** – Wants to know today’s working weather and time‑zone overlap with North‑America clients.
- **Alex (Budget Transit Nomad)** – Needs instant local currency rates and safety info before entering a neighboring country.

## 3. Success Metrics (KPIs)

- **Cold‑Start Load**: < 1 s to interactive dashboard (local DB hydration).
- **Offline Availability**: 100 % of core profiles, favorites, and packing lists available offline.
- **Cache Efficiency**: 0 % unnecessary network calls; dynamic metrics respect TTL.
- **Task Completion Velocity**: Currency conversion, time‑difference, or weather lookup within **3 taps** from launch.

## 4. End‑to‑End User Flow & Feature Spec

```
[ App Launch ] → [ First‑Time Seed / Profile Setup ]
      │
      ▼
[ Explore Dashboard ] → (Top Pills) → [ Filter by Continent / Favs / Themes ]
      │                ├─► [ Country Profile View ]
      │                └─► [ Trip Planner Tab ] (v2)
```

### EPIC 1 – Onboarding & System Hydration
- **1.1 First‑Launch Database Hydration** – Pulls `REST Countries` API data; falls back to bundled `seed_countries.json` when offline.
- **1.2 User Profile & Global Preferences** – Profile gear replaces search icon; stores name, home country, base currency, temperature unit (C/F/dual).

### EPIC 2 – Exploration & Discovery Hub
- **2.1 Global Search & Adaptive Filter Row** – Full‑text SQLite search, horizontal pill filters (All, My Favs, Continents, Themes).
- **2.2 Quick Favorites System** – Heart icon on country cards toggles favourite state instantly.

### EPIC 3 – Deep Destination Evaluation (Country Profile)
- **3.1 Hero Banner & Wikipedia Summary** – Cached indefinitely.
- **3.2 Safety Advisory Shield** – Color‑coded risk badge; tap reveals modal with full advisory.
- **3.3 Nomad Comfort Index (NCI)** – Score 1‑10 based on temperature, wind, UV; high‑UV tag when UV ≥ 6.
- **3.4 Interactive Currency Converter** – Glassmorphism pop‑up; uses Frankfurter API, fallback to cached `rates.json`.
- **3.5 Smart Time‑Zone Converter** – Pop‑up shows live times and hour delta.
- **3.6 Public & Bank Holidays** – Nager.Date API; next two holidays shown, “View All” expands full list.
- **3.7 Bordering Neighbor Explorer** – Carousel of neighboring nations; tap transitions to profile or side‑by‑side comparison.

### EPIC 4 – Version 2 Roadmap (Trip Planner & Customisation)
- Persistent bottom navigation (Explore / Trip Planner / Profile).
- Shortlisted itineraries and dynamic packing assistant with algorithmic suggestions (e.g., umbrella for precipitation > 40 %).

## 5. Technical Requirements & Architecture

### 5.1 Architecture Pattern
- Clean Architecture with Repository pattern:
  - **UI (Jetpack Compose / SwiftUI)** → **ViewModel / State** → **Use Cases** → **Repository** → **Local DB (Room / SwiftData)** & **Keyless Remote APIs**.

### 5.2 API Integration Matrix
| Domain | Endpoint | Auth | Cache TTL |
|---|---|---|---|
| Global Profiles | `restcountries.com/v3.1/all` | None | Indefinite (seed) |
| Live Weather | `api.open-meteo.com/v1/forecast` | None | 30 min |
| Exchange Rates | `api.frankfurter.dev/v1/latest` | None | 24 h |
| City Summary & Image | `en.wikipedia.org/api/rest_v1/page/summary/` | None | Indefinite |
| Public Holidays | `date.nager.at/api/v3/PublicHolidays/` | None | 30 days |
| Safety Advisories | `api.travel-advisory.info/api` | None | 24 h |

### 5.3 Local Database Schema (SQLite)
- `countries` (PK `cca3`): commonName, officialName, capital, region, subregion, flagUrl, isLandlocked, primaryCurrencyCode, primaryCurrencyName, primaryCurrencySymbol, languages.
- `country_borders` (PK `countryCca3`, `borderCca3`).
- `user_profile` (single row): userName, homeCountryCca3, baseCurrencyCode, tempUnit.
- `weather_cache`, `currency_cache`, `advisory_cache`, `holiday_cache` – each with TTL timestamps.

### 5.4 Offline Resilience Protocol
- Repository catches network errors; instantly falls back to cached SQLite data regardless of TTL.
- UI shows non‑intrusive “Showing Cached Data” badge on dynamic cards.

## 6. Non‑Functional Requirements & Constraints
- **Storage Footprint**: Seed DB ≤ 10 MB.
- **Rate Limiting**: Max 1 call/sec for batch fetches.
- **OS Compatibility**: Android 12 (API 31)+, iOS 16+.
- **Privacy**: Zero transmission of personal data; all profile data stored on‑device.

---
*Document version 2.0 – Final Comprehensive Build Specification*
