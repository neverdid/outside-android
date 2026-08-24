package com.neverdid.outside.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SampleDataTest {
    @Test
    fun activityIdsAreUnique() {
        val ids = SampleData.activities.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun activitiesNeverStartOverCapacity() {
        assertTrue(
            SampleData.activities.all { activity ->
                activity.going in 0..activity.capacity
            },
        )
    }

    @Test
    fun conversationsReferenceKnownMessageThreads() {
        val knownConversationIds = SampleData.conversations.map { it.id }.toSet()
        assertTrue(SampleData.messages.keys.all { it in knownConversationIds })
    }
}
