# NomadCompass – Architectural Document

## 1. Overview

NomadCompass is built as an **offline‑first cross‑platform mobile app** (Android 12+ / iOS 16+). The architecture follows **Clean Architecture** with a clear separation of concerns:

- **Presentation Layer** – Jetpack Compose (Android) / SwiftUI (iOS) UI components.
- **Domain Layer** – Use‑case inter‑actors, business logic, and domain models.
- **Data Layer** – Repositories abstracting data sources, handling synchronization, caching, and error handling.
- **Data Sources** –
  - **Local**: SQLite (Room / SwiftData) for persistent storage.
  - **Remote**: Keyless public REST APIs (REST Countries, Open‑Meteo, Frankfurter, Wikipedia, Nager.Date, Travel‑Advisory).

The diagram below illustrates the high‑level component interaction.

```
[ UI (Compose/SwiftUI) ]
      │          ▲
      ▼          │   (UI events)
[ ViewModel / State ]
      │          ▲
      ▼          │   (State flow)
[ Use‑Case / Interactor ]
      │          ▲
      ▼          │   (Domain requests)
[ Repository ]
   ├───────────────┐
   │               │
   ▼               ▼
[ Local DB ]   [ Remote API Service ]
```

## 2. Layer Responsibilities

| Layer | Responsibilities |
|------|--------------------|
| **Presentation** | Render UI, handle user interactions, expose observable state to UI components. |
| **ViewModel / State** | Convert domain models to UI models, manage UI‑specific state (loading, error flags). |
| **Domain / Use‑Case** | Encapsulate business rules (e.g., calculate Nomad Comfort Index, packing suggestions). |
| **Repository** | Decide whether to serve data from cache or remote, enforce TTL policies, map DTOs to domain entities. |
| **Data Sources** | Serialize/deserialize JSON, perform SQLite queries, manage network calls with exponential back‑off. |

## 3. Data Flow & Caching Strategy

1. **Read Path** – UI requests data → ViewModel calls Use‑Case → Repository checks **cache freshness** (based on TTL defined per API). If fresh, returns from SQLite; otherwise triggers remote fetch, updates cache, and returns data.
2. **Write Path** – User actions (e.g., favourite a country) → Use‑Case updates local DB directly; remote sync is not required because data is user‑specific.
3. **Offline Fallback** – Network errors are caught at Repository level, triggering immediate fallback to cached data regardless of TTL. UI displays a subtle “Showing Cached Data” badge.

## 4. Key Components

- **CountryRepository** – Provides methods `getAllCountries()`, `getCountryDetail(cca3)`, `search(query)`, `getFavorites()`.
- **WeatherRepository** – Supplies current weather + NCI calculations; respects 30 min TTL.
- **CurrencyRepository** – Retrieves latest rates (24 h TTL) and performs conversion calculations.
- **AdvisoryRepository** – Supplies safety scores (24 h TTL).
- **HolidayRepository** – Supplies public holidays (30 day TTL).
- **ProfileRepository** – Handles single‑row user profile storage.

## 5. Concurrency & Threading

- All I/O (DB, network) performed on Kotlin **Coroutines** (`Dispatchers.IO`).
- UI state updates confined to `Dispatchers.Main`.
- Rate‑limiting enforced via a **Token Bucket** per API (max 1 call/sec).

## 6. Dependency Injection

- Use **Hilt** (Android) / **Swinject** (iOS) to provide singleton instances of repositories, API services, and database DAOs.

## 7. Build & Packaging

- Gradle (Android) and Xcode (iOS) configured to include the bundled `seed_countries.json` in the assets/resource bundle.
- ProGuard / Swift compiler optimizations enabled for release builds.

## 8. Security & Privacy

- No analytics SDKs; all data remains on‑device.
- Network traffic is HTTPS‑only.
- No API keys required; all endpoints are public.

---
*Architecture version 2.0 – Final*
