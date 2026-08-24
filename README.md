# Outside

**Go outside more. Meet people who are into the same things.**

Outside is a native Android concept for turning shared hobbies into real-world plans. It helps people discover nearby activities, join small groups, talk before meeting, and stay connected afterward.

This repository contains a polished, interactive app foundation built with Kotlin, Jetpack Compose, and Material 3. It currently runs on local sample data, so the entire product flow is easy to explore before a backend is selected.

## What is included

- **Discover:** nearby plans, activity filters, search, plan details, RSVP, and a host-plan draft flow
- **Feed:** lightweight stories from recent activities with working reactions
- **Community:** searchable, category-based forum topics and a weekly conversation prompt
- **Direct messages:** individual and activity group conversations with locally sendable messages
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
├── model/           Activity, feed, forum, conversation models
├── ui/components/   Reusable cards, avatars, and plan composer
├── ui/screens/      Discover, feed, forum, inbox, chat, and detail UI
└── ui/theme/        Color, typography, and Material theme
```

## Next engineering milestones

1. Add authentication and interest/location onboarding.
2. Replace `SampleData` with repositories backed by Supabase or Firebase.
3. Add geospatial discovery, date/distance filters, and map links.
4. Add realtime DMs and per-activity group chats.
5. Add reporting, blocking, moderation, and host cancellation flows before public launch.
6. Add notifications, offline caching, UI tests, and analytics for the join funnel.

## Status

`0.1.0` — product and UI foundation. No production backend or user data is connected yet.
