# Outside

**Go outside more. Meet people who are into the same things.**

Outside is a native Android concept for turning shared hobbies into real-world plans. It helps people discover nearby activities, join small groups, talk before meeting, and stay connected afterward.

This repository contains a polished, interactive app foundation built with Kotlin, Jetpack Compose, Material 3, Firebase Authentication, and Cloud Firestore. It automatically uses a local demo backend when Firebase configuration is absent, so every contributor and CI build can still explore the complete product flow.

## What is included

- **Discover:** nearby plans, activity filters, search, plan details, persistent RSVP, and plan publishing
- **Feed:** personalized local stories with explainable ranking, filters, diversity, feedback controls, working reactions, and direct links to plans
- **Community:** searchable, category-based forum topics, a working topic composer, and a weekly conversation prompt
- **Direct messages:** realtime individual and activity group conversations with sendable messages
- **Onboarding:** welcome and email flows, first-name setup, approximate area and radius, interest selection, and self-described experience
- **Accounts:** Firebase email/password accounts and cloud profiles when configured, with a transparent on-device demo mode otherwise
- **Data layer:** replaceable repositories for activities, feed, forum, conversations, and session state
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
- Firebase Android BoM `34.18.0`, Authentication, and Cloud Firestore
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

Without `app/google-services.json`, the app displays **Demo account** in the profile and uses in-memory content plus an on-device session. To activate Firebase, follow [docs/MILESTONE_2_FIREBASE.md](docs/MILESTONE_2_FIREBASE.md).

## Project structure

```text
app/src/main/java/com/neverdid/outside/
├── content/         Shared content state and screen-level view model
├── data/            App container, backend selection, and demo data
│   ├── content/     Content contracts plus demo and Firebase implementations
│   └── session/     Local and Firebase session implementations
├── model/           Activity, community, conversation, and profile models
├── session/         Session state and view model
├── ui/onboarding/   Pure onboarding validation rules
├── ui/components/   Reusable cards, avatars, and plan composer
├── ui/screens/      Authentication, onboarding, profile, and core product UI
└── ui/theme/        Color, typography, and Material theme
```

## Next engineering milestones

1. **Complete:** authentication and interest/location onboarding.
2. **Implementation complete:** Firebase Authentication, Firestore repositories, realtime listeners, and security rules. A Firebase project configuration is required to activate the cloud path.
3. **Implementation complete:** a transparent feed ranker using interests, freshness, proximity, connection intent, capped quality, joined plans, and session diversity.
4. Add geospatial discovery, date/distance filters, and map links.
5. Add reporting, blocking, moderation, and host cancellation flows before public launch.
6. Add notifications, offline caching, UI tests, and analytics for the join funnel.

## Status

`0.4.0` — personalized feed and usability polish. The app now has a finite, explainable feed designed to turn inspiration into local plans. See [docs/FEED_RANKING.md](docs/FEED_RANKING.md).
