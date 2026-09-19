# Milestone 1: authentication and onboarding

## Outcome

The app now has a complete first-run path before a person reaches Discover:

```text
Welcome → create account/sign in → name → approximate area → interests → experience → Discover
```

Returning profiles skip onboarding. The Discover header uses the saved city and initials, and the avatar opens a profile screen with a sign-out action.

## What is implemented

- Welcome, create-account, and returning-user sign-in screens
- Inline email, password, name, area, and interest validation
- Four-step onboarding with progress and back navigation
- Approximate city/area and a 5–50 km discovery radius; no location permission or precise address
- At least two interests selected from the app’s activity taxonomy
- A self-described experience preference designed around comfort rather than ranking
- Persistent local session/profile storage with AndroidX Preferences DataStore
- Session state isolated behind `SessionRepository` and exposed through `SessionViewModel`
- Personalized Discover location and profile initials
- Profile summary and sign out
- Unit coverage for the pure onboarding validation rules

## Security boundary

This milestone deliberately does **not** pretend to provide server authentication. The password field validates the future production interaction, but passwords are never passed to or stored by the local repository. Only the normalized email, profile choices, and a local signed-in flag are stored on the device.

Milestone 2 added `FirebaseSessionRepository`. When Firebase configuration is present, credentials are sent directly to Firebase Authentication and application code never persists raw passwords. `LocalSessionRepository` remains only as the clearly labelled, zero-credential demo path.

## Backend seam

`SessionRepository` is the boundary for identity state:

```kotlin
interface SessionRepository {
    val currentProfile: Flow<UserProfile?>
    suspend fun authenticate(email: String, password: String, mode: AuthenticationMode)
    suspend fun completeOnboarding(/* profile fields */)
    suspend fun signOut()
}
```

The root UI reacts only to session states (`Loading`, `SignedOut`, `NeedsOnboarding`, and `Ready`). A cloud implementation can replace the local repository without rewriting the authentication, onboarding, profile, or core app screens.

## Acceptance checklist

- [x] A fresh install opens on the welcome page.
- [x] Invalid account fields show actionable feedback.
- [x] Onboarding cannot finish without a name, area, two interests, and an experience preference.
- [x] Completing onboarding opens the personalized Discover page.
- [x] Restarting the app restores the active profile.
- [x] Signing out returns to the welcome page without storing a password.
- [x] Signing back in with the same email restores the completed profile.
- [x] Production identity provider and remote profile record.
- [ ] Account recovery, email verification, and account deletion.
- [ ] Instrumented Compose tests on an emulator/device.
