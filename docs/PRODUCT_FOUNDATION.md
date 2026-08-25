# Outside: product foundation

## Product promise

Outside helps someone move from “I would like to do more things” to “I have a plan with people who enjoy the same thing.” It is not a follower network and it is not an event-ticket marketplace. The useful unit is a small, approachable plan.

## Why Discover is the main screen

A feed is good at showing what already happened, but it can make a new user feel like an observer. The main screen instead answers three practical questions immediately:

1. What can I do?
2. When and where is it happening?
3. Will I feel comfortable joining?

Every activity card therefore exposes time, distance, group size, pace/skill level, and the social “vibe” before asking for a tap.

## Core loops

### Plan loop

```text
Browse → filter → inspect a plan → RSVP → join group chat → attend
```

### Community loop

```text
Ask a low-stakes question → get local advice → discover people → make a plan
```

### Retention loop

```text
Attend → share a small moment → inspire another plan → reconnect
```

## MVP scope

### Required for a closed beta

- Email/social authentication
- Profile with first name, photo, home area, interests, and self-described skill levels
- Activity creation, editing, cancellation, capacity, and RSVP state
- Discovery by approximate location, date, distance, category, and level
- One-to-one messages and automatically created activity group chats
- Forum categories, topics, replies, and search
- Feed posts tied to an activity or “looking for people” intent
- Push notifications for RSVP changes, messages, and plan updates
- Blocking, reporting, basic moderation queue, and community guidelines

### Intentionally postponed

- Public follower counts
- Algorithmic creator ranking
- Paid events and ticketing
- Complex clubs/organizations
- Streaks that punish rest days
- Precise live-location sharing

## Suggested data model

| Entity | Important fields |
| --- | --- |
| User | id, name, avatar, approximate area, interests, skill levels, trust state |
| Activity | id, host, title, category, description, start/end, meeting point, visibility, capacity, level, status |
| RSVP | activity id, user id, status, created at |
| Conversation | id, type (`direct`/`activity`), activity id, last message at |
| ConversationMember | conversation id, user id, joined at, muted until |
| Message | id, conversation id, sender, body, attachment, sent at, moderation state |
| ForumTopic | id, author, category, title, body, created at, status |
| ForumReply | id, topic id, author, body, created at, status |
| FeedPost | id, author, activity id, body, media, created at, visibility |
| Report | id, reporter, subject type/id, reason, status, created at |

Store only approximate discovery coordinates publicly. Reveal a specific meeting point only when the host chooses to, ideally after RSVP for sensitive locations.

## Architecture path

The UI currently reads immutable activity/community sample models and keeps those interactions in Compose. Session and profile state now use a `SessionRepository`, an AndroidX DataStore-backed local implementation, and a screen-independent view model. The next backend step should preserve that boundary while introducing `ActivityRepository`, `ConversationRepository`, and `CommunityRepository` interfaces.

A pragmatic early backend can use either:

- **Firebase:** strong Android SDKs, auth, realtime chat, notifications, and analytics in one ecosystem.
- **Supabase:** relational/Postgres model, row-level security, realtime, and more direct SQL/geospatial control.

Choose after validating whether the team values mobile-integrated infrastructure speed or relational/geospatial transparency more. Keep the domain interfaces independent either way.

## Trust and safety baseline

This is a meet-in-real-life product, so safety is not a later polish item.

- Default to public meeting points and approximate discovery distance.
- Allow block/report from profiles, messages, topics, replies, and activities.
- Prevent a blocked user from discovering or messaging the blocker.
- Preserve evidence for reviewed reports while respecting retention limits.
- Add host cancellation and attendee notification flows.
- Rate-limit new accounts, unsolicited DMs, and rapid activity creation.
- Publish clear guidelines and a real escalation path before opening broadly.
- Avoid implying that identity or attendance has been verified until it actually has.

## First metrics

The primary success metric should be **weekly unique users who join or host an activity**, not time spent in the app.

Supporting funnel metrics:

- Discovery view → activity detail rate
- Activity detail → RSVP rate
- RSVP → first group-chat message rate
- RSVP → self-reported attendance rate
- First attendance → second plan within 30 days
- Report/block rate per 1,000 conversations and activities

Use the feed and forum only as healthy drivers of those outcomes; do not optimize them for passive scrolling.
