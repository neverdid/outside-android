package com.neverdid.outside.feed

import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.model.FeedPostIntent
import kotlin.math.exp
import kotlin.math.ln
import java.util.Locale

data class FeedRankingContext(
    val interests: Set<ActivityCategory>,
    val radiusKm: Int,
    val likedPostIds: Set<String>,
    val joinedActivityIds: Set<String>,
    val joinedCategories: Set<ActivityCategory>,
    val hiddenPostIds: Set<String> = emptySet(),
    val demotedCategories: Set<ActivityCategory> = emptySet(),
    val sessionSeed: Int = 0,
)

data class RankedFeedPost(
    val post: FeedPost,
    val score: Double,
    val reason: String,
)

/**
 * A transparent first-stage ranker for a small beta.
 *
 * The objective is a useful local connection: interest fit, freshness, proximity and
 * actionable intent carry most of the score. Popularity is deliberately capped so a few
 * established posters cannot dominate the feed. A second pass diversifies adjacent cards.
 */
object FeedRanker {
    fun rank(
        posts: List<FeedPost>,
        context: FeedRankingContext,
        nowEpochMillis: Long = System.currentTimeMillis(),
    ): List<RankedFeedPost> {
        val likedCategoryCounts = posts
            .asSequence()
            .filter { it.id in context.likedPostIds }
            .groupingBy { it.category }
            .eachCount()

        val candidates = posts
            .asSequence()
            .filterNot { it.id in context.hiddenPostIds }
            .map { post ->
                val signals = scoreSignals(post, context, likedCategoryCounts, nowEpochMillis)
                RankedFeedPost(
                    post = post,
                    score = signals.total + explorationNudge(post.id, context.sessionSeed),
                    reason = explanation(post, context, signals.ageHours),
                )
            }
            .toMutableList()

        return buildList(candidates.size) {
            while (candidates.isNotEmpty()) {
                val next = candidates.maxBy { candidate ->
                    candidate.score - diversityPenalty(candidate.post, takeLast(2))
                }
                add(next)
                candidates.remove(next)
            }
        }
    }

    private fun scoreSignals(
        post: FeedPost,
        context: FeedRankingContext,
        likedCategoryCounts: Map<ActivityCategory, Int>,
        nowEpochMillis: Long,
    ): Signals {
        val learnedAffinity = (likedCategoryCounts[post.category] ?: 0) * 0.14
        val interest = (
            (if (post.category in context.interests) 0.72 else 0.12) +
                (if (post.category in context.joinedCategories) 0.24 else 0.0) +
                learnedAffinity
            ).coerceIn(0.0, 1.0)

        val ageHours = if (post.createdAtEpochMillis <= 0L) {
            null
        } else {
            ((nowEpochMillis - post.createdAtEpochMillis).coerceAtLeast(0L) / 3_600_000.0)
        }
        val freshness = ageHours?.let { exp(-it / 72.0) } ?: 0.35

        val radius = context.radiusKm.coerceAtLeast(1).toDouble()
        val proximity = post.distanceKm
            ?.let { (1.0 - it / (radius * 1.4)).coerceIn(0.0, 1.0) }
            ?: 0.35

        val actionability = when (post.intent) {
            FeedPostIntent.LOOKING_FOR_PEOPLE -> 1.0
            FeedPostIntent.QUESTION -> 0.78
            FeedPostIntent.TIP -> 0.62
            FeedPostIntent.PLAN_RECAP -> 0.50
            FeedPostIntent.MOMENT -> 0.38
        }
        val quality = (
            ln(1.0 + post.reactions + post.comments * 2.0) / ln(81.0)
            ).coerceIn(0.0, 1.0)
        val joinedPlan = if (
            post.relatedActivityId?.let { it in context.joinedActivityIds } == true
        ) {
            1.0
        } else {
            0.0
        }
        val alreadyLikedPenalty = if (post.id in context.likedPostIds) 0.04 else 0.0
        val negativeFeedbackPenalty = if (post.category in context.demotedCategories) 0.12 else 0.0

        return Signals(
            total = interest * 0.30 +
                freshness * 0.23 +
                proximity * 0.18 +
                actionability * 0.16 +
                quality * 0.08 +
                joinedPlan * 0.05 -
                alreadyLikedPenalty -
                negativeFeedbackPenalty,
            ageHours = ageHours,
        )
    }

    private fun explanation(
        post: FeedPost,
        context: FeedRankingContext,
        ageHours: Double?,
    ): String = when {
        post.relatedActivityId?.let { it in context.joinedActivityIds } == true ->
            "From a plan you joined"
        post.category in context.interests -> "Because you like ${post.category.label.lowercase()}"
        post.intent == FeedPostIntent.LOOKING_FOR_PEOPLE -> "Looking for people nearby"
        post.distanceKm != null && post.distanceKm <= context.radiusKm / 2.0 ->
            "${formatDistance(post.distanceKm)} km away"
        ageHours != null && ageHours < 24.0 -> "New today"
        else -> "Popular near you"
    }

    private fun diversityPenalty(post: FeedPost, recent: List<RankedFeedPost>): Double {
        val previous = recent.lastOrNull()?.post
        val sameCategory = if (recent.any { it.post.category == post.category }) 0.11 else 0.0
        val sameAuthor = if (
            previous != null &&
            (previous.authorId.ifBlank { previous.author }) == (post.authorId.ifBlank { post.author })
        ) {
            0.15
        } else {
            0.0
        }
        val sameIntent = if (recent.any { it.post.intent == post.intent }) 0.04 else 0.0
        return sameCategory + sameAuthor + sameIntent
    }

    private fun explorationNudge(postId: String, sessionSeed: Int): Double {
        val mixed = (postId.hashCode() xor sessionSeed).toUInt().toDouble()
        return (mixed / UInt.MAX_VALUE.toDouble() - 0.5) * 0.03
    }

    private fun formatDistance(distance: Double): String =
        if (distance % 1.0 == 0.0) {
            distance.toInt().toString()
        } else {
            String.format(Locale.getDefault(), "%.1f", distance)
        }

    private data class Signals(
        val total: Double,
        val ageHours: Double?,
    )
}
