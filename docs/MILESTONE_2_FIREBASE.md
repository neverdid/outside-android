# Milestone 2: Firebase backend

## Outcome

Outside now has two explicit runtime modes:

```text
app/google-services.json present → Firebase Auth + Cloud Firestore
app/google-services.json absent  → on-device session + in-memory demo repositories
```

The fallback is intentional: a fresh clone and GitHub Actions remain buildable without sharing cloud credentials, while a configured build uses production authentication and realtime content flows.

## What is implemented

- Email/password account creation and sign-in with Firebase Authentication
- Realtime user-profile documents in `users/{uid}`
- Repository contracts for activities, feed posts, topics, conversations, and messages
- Realtime Firestore listeners for all core surfaces
- Firestore transactions for RSVP and feed-like counters
- Firestore writes for hosted plans, forum topics, and chat messages
- A real forum-topic composer and location-aware plan publisher
- Loading and actionable authentication errors
- Backend mode shown honestly on the profile and authentication screens
- Deny-by-default Firestore rules with owner/member checks
- Firebase CLI configuration, rules, and indexes in the repository root
- CI checks for Android tests/lint/build and Firestore rule compilation
- Demo repository tests covering RSVP, hosting, likes, and messages

## Activate Firebase

1. Create a Firebase project in the [Firebase console](https://console.firebase.google.com/).
2. Add an Android application with package name `com.neverdid.outside`.
3. Download `google-services.json` and place it at `app/google-services.json`.
4. In **Authentication → Sign-in method**, enable **Email/Password**.
5. Create a Cloud Firestore database in **Native mode**. Choose the region deliberately; changing it later requires a new database.
6. Install and authenticate the Firebase CLI, then select the project and deploy the checked-in rules:

   ```bash
   firebase login
   firebase use --add
   firebase deploy --only firestore
   ```

7. Rebuild the app. The profile screen should say **Firebase account** instead of **Demo account**.

`google-services.json` contains project identifiers rather than a server secret, but it is ignored here so forks and CI are not silently tied to one production environment. Never commit service-account keys.

## Firestore collections

| Path | Important fields | Client access |
| --- | --- | --- |
| `users/{uid}` | email, firstName, city, radiusKm, interests, experience, onboardingComplete | owner only |
| `activities/{id}` | hostId, title, category, attendeeIds, going, capacity, status | authenticated read; host create/manage; members join/leave themselves |
| `feedPosts/{id}` | authorId, text, likedBy, reactions, activityLabel | authenticated read; author manage; members like/unlike themselves |
| `forumTopics/{id}` | authorId, title, body, category, status | authenticated read; author create/manage |
| `conversations/{id}` | memberIds, name, type, lastMessage, lastMessageAt | members only |
| `conversations/{id}/messages/{id}` | senderId, senderName, body, sentAt, moderationState | members read; sender creates immutable messages |

The mobile mappers tolerate optional presentation fields so seed data can grow gradually. New plans and topics can be created directly from the app. A conversation must contain the signed-in UID in `memberIds` before it appears in Inbox.

## Security notes

- Rules default to no access for unknown paths.
- Profiles are private to their owner; public profile projections should become a separate collection when needed.
- RSVP and like updates may change only the signed-in user’s own membership and the corresponding counter.
- Message documents are immutable from the client and limited to 4,000 characters.
- The client initially marks new messages `pending`; server-side moderation can promote that state later.
- Admin SDKs bypass Firestore rules. Keep all service-account material outside the repository and use least-privilege deployment identities.

## Acceptance checklist

- [x] The project builds and remains usable without Firebase configuration.
- [x] Static `SampleData` references are isolated behind demo repositories.
- [x] Configured builds select Firebase automatically.
- [x] Auth UI supports distinct create-account and sign-in operations.
- [x] Profiles, activities, feed, topics, inbox, and chat are repository-driven.
- [x] RSVP, likes, hosting, topics, and sent messages have cloud write paths.
- [x] Security rules and deployment configuration are checked in.
- [x] Firestore rules compile successfully in the local emulator and CI checks them on pull requests.
- [ ] A Firebase project is created and `google-services.json` is supplied locally.
- [ ] Rules are deployed and the remote end-to-end flow is exercised with two test accounts.
- [ ] Email verification, password reset, account deletion, and App Check are added before public beta.
