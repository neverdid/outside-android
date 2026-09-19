package com.neverdid.outside.data.content

import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ExperienceLevel
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoContentRepositoriesTest {
    private val profile = UserProfile(
        id = "test-user",
        email = "alex@example.com",
        firstName = "Alex",
        city = "Brașov",
        interests = setOf(ActivityCategory.HIKING, ActivityCategory.CAMPING),
        experience = ExperienceLevel.CASUAL,
        onboardingComplete = true,
    )

    @Test
    fun `joining a plan updates membership and attendee count`() = runBlocking {
        val repository = DemoActivityRepository()
        val activity = repository.activities.first().first()

        repository.toggleJoin(activity.id)

        assertTrue(activity.id in repository.joinedActivityIds.first())
        assertEquals(activity.going + 1, repository.activities.first().first().going)
    }

    @Test
    fun `hosting a plan publishes it and joins the host`() = runBlocking {
        val repository = DemoActivityRepository()

        repository.createActivity(
            NewActivity("Sunday forest loop", "Noua trailhead", ActivityCategory.HIKING),
            profile,
        )

        val activity = repository.activities.first().first()
        assertEquals("Sunday forest loop", activity.title)
        assertEquals("Alex", activity.host)
        assertTrue(activity.id in repository.joinedActivityIds.first())
    }

    @Test
    fun `sent messages update both chat and inbox preview`() = runBlocking {
        val repository = DemoConversationRepository()
        val conversation = repository.conversations.first().first()

        repository.sendMessage(conversation.id, "I’ll bring an extra bottle.", profile)

        assertEquals("I’ll bring an extra bottle.", repository.messages(conversation.id).first().last().body)
        assertEquals("I’ll bring an extra bottle.", repository.conversations.first().first().preview)
    }

    @Test
    fun `liking a post is reversible`() = runBlocking {
        val repository = DemoFeedRepository()
        val postId = repository.posts.first().first().id

        repository.toggleLike(postId)
        assertTrue(postId in repository.likedPostIds.first())

        repository.toggleLike(postId)
        assertTrue(postId !in repository.likedPostIds.first())
    }
}
