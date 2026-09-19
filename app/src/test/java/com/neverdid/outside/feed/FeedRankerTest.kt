package com.neverdid.outside.feed

import com.neverdid.outside.model.ActivityAccent
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.model.FeedPostIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedRankerTest {
    private val now = 2_000_000_000_000L

    @Test
    fun `fresh local interest match beats stale popularity`() {
        val useful = post(
            id = "useful",
            category = ActivityCategory.HIKING,
            intent = FeedPostIntent.LOOKING_FOR_PEOPLE,
            ageHours = 2,
            distanceKm = 2.0,
            reactions = 4,
        )
        val viralButIrrelevant = post(
            id = "viral",
            category = ActivityCategory.CYCLING,
            intent = FeedPostIntent.MOMENT,
            ageHours = 24 * 10,
            distanceKm = 80.0,
            reactions = 5_000,
        )

        val ranked = FeedRanker.rank(
            posts = listOf(viralButIrrelevant, useful),
            context = context(interests = setOf(ActivityCategory.HIKING)),
            nowEpochMillis = now,
        )

        assertEquals("useful", ranked.first().post.id)
    }

    @Test
    fun `adjacent results are diversified when alternatives are competitive`() {
        val posts = listOf(
            post("hike-1", ActivityCategory.HIKING, authorId = "same-host"),
            post("hike-2", ActivityCategory.HIKING, authorId = "same-host"),
            post("camp", ActivityCategory.CAMPING, authorId = "another-host"),
        )

        val ranked = FeedRanker.rank(
            posts = posts,
            context = context(
                interests = setOf(ActivityCategory.HIKING, ActivityCategory.CAMPING),
            ),
            nowEpochMillis = now,
        )

        assertFalse(ranked[0].post.category == ranked[1].post.category)
        assertFalse(ranked[0].post.authorId == ranked[1].post.authorId)
    }

    @Test
    fun `hidden posts are removed from the session`() {
        val ranked = FeedRanker.rank(
            posts = listOf(post("hidden", ActivityCategory.HIKING), post("visible", ActivityCategory.CAMPING)),
            context = context(hiddenPostIds = setOf("hidden")),
            nowEpochMillis = now,
        )

        assertEquals(listOf("visible"), ranked.map { it.post.id })
    }

    @Test
    fun `negative feedback gently demotes similar posts`() {
        val hiking = post("hiking", ActivityCategory.HIKING)
        val camping = post("camping", ActivityCategory.CAMPING)

        val ranked = FeedRanker.rank(
            posts = listOf(hiking, camping),
            context = context().copy(demotedCategories = setOf(ActivityCategory.HIKING)),
            nowEpochMillis = now,
        )

        assertEquals("camping", ranked.first().post.id)
    }

    @Test
    fun `joined plan is explained to the member`() {
        val ranked = FeedRanker.rank(
            posts = listOf(
                post("joined", ActivityCategory.RUNNING).copy(relatedActivityId = "morning-run"),
            ),
            context = context(joinedActivityIds = setOf("morning-run")),
            nowEpochMillis = now,
        )

        assertTrue(ranked.single().reason.contains("plan you joined"))
    }

    private fun context(
        interests: Set<ActivityCategory> = emptySet(),
        joinedActivityIds: Set<String> = emptySet(),
        hiddenPostIds: Set<String> = emptySet(),
    ) = FeedRankingContext(
        interests = interests,
        radiusKm = 20,
        likedPostIds = emptySet(),
        joinedActivityIds = joinedActivityIds,
        joinedCategories = emptySet(),
        hiddenPostIds = hiddenPostIds,
        sessionSeed = 42,
    )

    private fun post(
        id: String,
        category: ActivityCategory,
        intent: FeedPostIntent = FeedPostIntent.MOMENT,
        ageHours: Int = 3,
        distanceKm: Double = 3.0,
        reactions: Int = 12,
        authorId: String = id,
    ) = FeedPost(
        id = id,
        author = authorId,
        initials = "O",
        timeAgo = "$ageHours hr",
        text = "A useful local post",
        activityLabel = category.label.uppercase(),
        reactions = reactions,
        comments = 3,
        accent = ActivityAccent.FOREST,
        authorId = authorId,
        category = category,
        intent = intent,
        createdAtEpochMillis = now - ageHours * 3_600_000L,
        distanceKm = distanceKm,
    )
}
