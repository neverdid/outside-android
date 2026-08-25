# Outside

**Go outside more. Meet people who are into the same things.**

Outside is a native Android concept for turning shared hobbies into real-world plans. It helps people discover nearby activities, join small groups, talk before meeting, and stay connected afterward.

This repository contains a polished, interactive app foundation built with Kotlin, Jetpack Compose, and Material 3. Activity and community content currently use local sample data, so the entire product flow is easy to explore before a backend is selected.

## What is included

- **Discover:** nearby plans, activity filters, search, plan details, RSVP, and a host-plan draft flow
- **Feed:** lightweight stories from recent activities with working reactions
- **Community:** searchable, category-based forum topics and a weekly conversation prompt
- **Direct messages:** individual and activity group conversations with locally sendable messages
- **Onboarding:** welcome and email flows, first-name setup, approximate area and radius, interest selection, and self-described experience
- **Local session:** the completed profile persists across restarts, returning users can sign back in, and sign-out is available from the profile screen
- **Thoughtful defaults:** beginner-friendly language, visible group size, public meeting guidance, and no popularity-first profile metrics
- **Foundation:** Compose theme, reusable components, domain models, mock data, unit tests, and CI

## Product direction

The home screen is intentionally **plan-first**, not feed-first. The shortest path to the product goal is:

```text
Discover something nearby → understand the vibe → join → talk to the group → show up
```

The feed supports retention, the forum supports confidence and knowledge-sharing, and messages turn interest into coordination. See [docs/PRODUCT_FOUNDATION.md](docs/PRODUCT_FOUNDATION.md) for the rationale, MVP boundaries, data model, trust-and-safety requirements, and recommended backend sequence.

## Tech stack

- Kotlin with Android Gradle Plugin 9.3 built-in Kotlin support
- Jetpack Compose + Material 3
- Compose BOM `2026.08.00`
- Minimum Android 8.0 (API 26), target/compile API 37
- JDK 17 and Gradle 9.5

## Run the app

1. Install the latest stable Android Studio.
2. Open this repository as an existing project.
3. Let Gradle sync and install Android SDK 37 if prompted.
4. Run the `app` configuration on an emulator or Android device.

From a terminal with Android SDK 37 and JDK 17 configured:

```bash
./gradlew assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Project structure

```text
app/src/main/java/com/neverdid/outside/
├── data/            Local sample repository
│   └── session/     Replaceable session repository and DataStore implementation
├── model/           Activity, community, conversation, and profile models
├── session/         Session state and view model
├── ui/onboarding/   Pure onboarding validation rules
├── ui/components/   Reusable cards, avatars, and plan composer
├── ui/screens/      Authentication, onboarding, profile, and core product UI
└── ui/theme/        Color, typography, and Material theme
```

## Next engineering milestones

1. **In progress:** authentication and interest/location onboarding. The full local flow and backend seam are complete; production identity is the remaining part.
2. Replace `SampleData` with repositories backed by Supabase or Firebase, including production authentication.
3. Add geospatial discovery, date/distance filters, and map links.
4. Add realtime DMs and per-activity group chats.
5. Add reporting, blocking, moderation, and host cancellation flows before public launch.
6. Add notifications, offline caching, UI tests, and analytics for the join funnel.

## Status

`0.2.0` — local authentication/onboarding milestone. Profiles persist only on the device; no production backend or remote user data is connected yet. See [docs/MILESTONE_1_AUTH_ONBOARDING.md](docs/MILESTONE_1_AUTH_ONBOARDING.md).
