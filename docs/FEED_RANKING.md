# Feed ranking and healthy engagement

## Product objective

The feed should make someone more likely to meet people and go outside. Session length is a useful diagnostic only when it leads to a high-intent action; it is not the primary objective.

The optimization order is:

1. open or join a relevant plan;
2. start a useful conversation or reply to someone looking for people;
3. discover a new local hobby or person;
4. react to a post;
5. passive dwell time.

This avoids an addictive infinite-scroll loop that would conflict with the product promise.

## Ranking model

`FeedRanker` is a deterministic, on-device first-stage ranker. It makes the beta behavior inspectable and testable before there is enough data for a learned model.

| Signal | Weight | Behavior |
| --- | ---: | --- |
| Hobby affinity | 30% | Explicit onboarding interests, followed by categories from joined and liked content |
| Freshness | 23% | Exponential decay with a 72-hour time constant |
| Proximity | 18% | Scores within the member’s chosen discovery radius most strongly |
| Connection intent | 16% | Prioritizes people actively looking for company, then questions and useful tips |
| Quality | 8% | Log-scaled replies and reactions, capped so popularity cannot dominate |
| Joined-plan context | 5% | Keeps posts from an RSVP’d plan visible for coordination and belonging |

Already-liked items receive a small novelty penalty. A deterministic exploration nudge breaks ties without reshuffling the feed on every recomposition.

After scoring, a greedy diversity pass penalizes adjacent posts from the same category, author, or intent. That prevents a hiking-heavy onboarding choice or one prolific host from turning the entire session into duplicates.

## User controls and explanations

- **For you** uses the complete multi-signal ranker.
- **Nearby** sorts available results by distance.
- **New** sorts by creation time.
- **Find people** shows explicit people-seeking posts.
- Every card explains one strong reason it was selected.
- “Show me less like this” removes an item for the current session.
- Related posts link directly to their plan.
- The finite feed ends with “You’re caught up” and an Explore call to action.

The current demo has seven posts spanning multiple categories and intents. Firebase documents can supply the same signals with `authorId`, `category`, `intent`, `createdAt`, `distanceKm`, and `activityId`. Missing values get neutral fallbacks so older documents remain usable.

## Measuring whether it works

Before tuning weights, add privacy-conscious events for:

- feed impression with rank position and reason;
- card dwell bucket, not a continuous behavioral trace;
- reaction, hide, and filter selection;
- related-plan open;
- RSVP or message within the same session;
- self-reported attendance later.

Use **weekly users who join or host a plan** as the north-star metric. Feed-to-plan opens, replies to people-seeking posts, and RSVP conversion are leading indicators. Track hides, reports, category concentration, and time-to-first-useful-result as guardrails.

Only consider a learned ranker after the beta has enough unbiased impressions. Train on downstream connection outcomes rather than raw watch time, reserve some randomized traffic for evaluation, and audit results by new-user status, activity category, distance band, and experience level.

## Next implementation steps

1. Persist hides and category preferences per account.
2. Add post creation, comments, report, and block flows.
3. Record ranked impressions and downstream plan actions with consent-aware analytics.
4. Replace precomputed `distanceKm` with privacy-preserving geospatial discovery.
5. Add server-side candidate retrieval before the on-device re-ranking step when the collection grows beyond a small beta.
