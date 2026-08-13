# NomadCompass – Design Document

## 1. Design Philosophy

- **Premium, modern aesthetic** – Dark‑mode base with pastel accent hues, soft rounded shapes, subtle depth via inner shadows, and micro‑animations that convey a high‑quality, trustworthy travel companion.
- **Consistency** – A single design system provides colour tokens, typography, spacing, and component styles across Android (Compose) and iOS (SwiftUI).
- **Accessibility** – Sufficient colour contrast, scalable type (dynamic type on iOS, font‑scale on Android), and screen‑reader friendly labels.

## 2. Visual Language

| Token | Value | Usage |
|-------|-------|-------|
| **Primary Background** | `hsl(210, 15%, 10%)` (dark) | App background, navigation bar. |
| **Surface (Clay)** | `hsl(30, 20%, 90%)` | Card surfaces with subtle inner shadow, giving a soft‑clay appearance. |
| **Accent** | `hsl(210, 80%, 55%)` | Interactive elements (buttons, pills). |
| **Success** | `hsl(120, 70%, 45%)` | Safety shield ✅. |
| **Warning** | `hsl(45, 90%, 55%)` | High‑UV tag. |
| **Error** | `hsl(0, 80%, 55%)` | Critical warnings. |
| **Typography** | **Google Font – Inter** (weights 400‑700) | Primary text; falls back to system font. |
| **Elevation** | Soft drop‑shadow `0dp‑6dp` on cards, creating a subtle levitation effect. |

## 3. Core UI Screens

### 3.1 Launch / Hydration Screen
- Full‑screen splash with the **NomadCompass** logo.
- Animated progress bar indicating database seeding (if first launch).
- Transitions to **Profile Setup** if no user profile exists.
- Background uses a subtle clay‑texture pattern with pastel overlay.

### 3.2 Profile Setup
- Simple form with fields: Name, Home Country (dropdown), Base Currency (auto‑filled, editable), Temperature Unit (toggle C/F/Dual).
- “Save & Continue” button with a **soft‑scale** ripple animation.
- Validation feedback displayed as red inline messages.

### 3.3 Explore Dashboard (Home)
- **Top Pill Bar** – Horizontal scrollable pills (All, My Favs, Continents, Themes). Selected pill highlighted with accent colour and a gentle underline slide.
- **Search Bar** – Persistent, expands on focus; includes clear‑icon button.
- **Country Card Grid** – Two‑column staggered grid on phones, three‑column on tablets.
  - Card components: Flag thumbnail, country name, favourite heart (outline ↔ filled) with a **soft‑pop** animation.
  - Cards have a **clay‑morphism** surface: pastel background, rounded corners, subtle inner shadow.
  - Card tap triggers Hero transition to Country Profile.
- **Bottom Navigation** – Persistent three‑tab bar (Explore, Trip Planner, Profile) with a pastel‑colored background, icons with a gentle elevation lift on press.

### 3.4 Country Profile Screen
- **Hero Banner** – Full‑width image from Wikipedia, overlayed with country name & capital.
- **Safety Shield Badge** – Circular badge (green/yellow/red) left‑aligned, with tooltip on tap.
- **Nomad Comfort Index** – Horizontal progress bar (10‑segment) tinted based on score, with animated fill.
- **Info Sections** (stacked vertically):
  1. **Weather Card** – Dual temperature display, icon, UV badge.
  2. **Currency Card** – Local currency, conversion button opens a **clay‑styled** pop‑up.
  3. **Timezone Card** – Current time, delta info, pop‑up for comparison.
  4. **Public Holidays** – Collapsible accordion showing next two holidays; “View All” expands modal.
  5. **Neighbor Carousel** – Horizontal scroll, each neighbor card includes flag and short‑name; tap animates a slide‑in transition.
- **Floating Action Button** – “Add to Itinerary” (plus icon) anchored bottom‑right, with a subtle **pulse** animation.

### 3.5 Trip Planner (v2)
- **Itinerary List** – Card list showing saved destinations, each with an editable packing list.
- **Packing Assistant** – Auto‑generated items based on weather & geography; each item shows a micro‑animation when added.
- **Add Custom Item** – Text input with **chip‑style** button.

### 3.6 Settings / Profile Tab
- Editable profile fields, toggles for dark/light mode, data‑clear button.
- “About” section with app version, legal links.

## 4. Interaction Patterns & Micro‑Animations

| Interaction | Animation | Description |
|-------------|-----------|-------------|
| Card Tap | Hero‑image shared‑element transition (0.3s) | Smooth visual continuity from dashboard to profile. |
| Favorite Heart | Soft‑pop (0.15s) + colour fade | Immediate feedback on favourite state. |
| Pill Selection | Underline slide (0.25s) | Highlights active filter. |
| Currency Pop‑up | Fade‑in + pastel overlay (0.2s) | Emphasises modal nature without harsh borders. |
| Bottom Nav Icon Press | Gentle elevation lift (0.1s) | Tactile feel. |
| Loading Skeletons | Subtle shimmer on cards while data loads | Reduces perceived latency. |
| Button Press | Light bounce (0.12s) | Gives a friendly, tactile response. |

## 5. Component Library (Design System)

- **Button** – Primary (filled accent with soft shadow), Secondary (outlined pastel), Icon‑only with soft‑scale effect.
- **Card** – Clay‑style surface with rounded corners, pastel background, inner shadow for depth.
- **Badge** – Circular, colour‑coded status indicators.
- **Accordion** – Collapsible panels with chevron rotation and smooth height animation.
- **Carousel** – Horizontal scroll with snapping and subtle depth shift on active item.
- **Modal** – Centered pastel dialog with slight elevation, dismissible via backdrop tap.

All components expose light/dark variants and accept theming tokens via a `Theme` object.

## 6. Accessibility Considerations

- **Contrast** – Minimum 4.5:1 for normal text, 3:1 for large text.
- **Content Descriptions** – All icons have `contentDescription` for screen readers.
- **Dynamic Font Scaling** – UI respects system font size settings.
- **Touch Targets** – Minimum 48 dp for interactive elements.

## 7. Asset Generation

- Icons derived from Material Design icons, tinted with accent colour.
- Placeholder images created via the `generate_image` tool for mock‑ups (e.g., `hero_banner_mockup`).
- All assets stored under `res/drawable` (Android) and `Assets.xcassets` (iOS).

---
*Design Document version 2.1 – Updated to Claymorphism UI style*
