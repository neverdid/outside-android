package com.neverdid.outside.data.content.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.neverdid.outside.data.content.ActivityRepository
import com.neverdid.outside.data.content.NewActivity
import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.ActivityAccent
import com.neverdid.outside.model.UserProfile
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await

class FirebaseActivityRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    scope: CoroutineScope,
) : ActivityRepository {
    private val documents = firestore.collection(ACTIVITIES)
        .whereEqualTo("status", "active")
        .documentSnapshots()
        .shareIn(scope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    override val activities: Flow<List<Activity>> = documents.map { snapshots ->
        snapshots.mapNotNull { it.toActivity() }
    }

    override val joinedActivityIds: Flow<Set<String>> = documents.map { snapshots ->
        val userId = auth.currentUser?.uid.orEmpty()
        snapshots.filter { it.containsUser("attendeeIds", userId) }.mapTo(mutableSetOf()) { it.id }
    }

    override suspend fun toggleJoin(activityId: String) {
        val userId = checkNotNull(auth.currentUser?.uid) { "Sign in to join a plan." }
        val reference = firestore.collection(ACTIVITIES).document(activityId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(reference)
            val attendees = snapshot.get("attendeeIds").stringList()
            val currentGoing = snapshot.getLong("going")?.toInt() ?: attendees.size
            val capacity = snapshot.getLong("capacity")?.toInt() ?: Int.MAX_VALUE
            if (userId in attendees) {
                transaction.update(
                    reference,
                    mapOf(
                        "attendeeIds" to attendees - userId,
                        "going" to (currentGoing - 1).coerceAtLeast(0),
                        "updatedAt" to FieldValue.serverTimestamp(),
                    ),
                )
            } else {
                check(currentGoing < capacity) { "This plan is already full." }
                transaction.update(
                    reference,
                    mapOf(
                        "attendeeIds" to attendees + userId,
                        "going" to currentGoing + 1,
                        "updatedAt" to FieldValue.serverTimestamp(),
                    ),
                )
            }
        }.await()
    }

    override suspend fun createActivity(draft: NewActivity, host: UserProfile) {
        val userId = checkNotNull(auth.currentUser?.uid) { "Sign in to host a plan." }
        val date = LocalDate.now().plusDays(1)
        firestore.collection(ACTIVITIES).add(
            mapOf(
                "title" to draft.title.trim(),
                "category" to draft.category.name,
                "hostId" to userId,
                "hostName" to host.firstName,
                "hostInitials" to host.initials,
                "day" to "NEW",
                "date" to date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)).uppercase(),
                "time" to "TBD",
                "location" to draft.location.trim(),
                "distance" to "Near ${host.city}",
                "going" to 1,
                "capacity" to 8,
                "vibe" to "Friendly pace · Newcomers welcome",
                "description" to "A new plan hosted by ${host.firstName}. Message the host to coordinate the details.",
                "bring" to listOf("Water", "Weather-ready layers"),
                "accent" to ActivityAccent.entries[draft.category.ordinal % ActivityAccent.entries.size].name,
                "attendeeIds" to listOf(userId),
                "status" to "active",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
    }

    private companion object {
        const val ACTIVITIES = "activities"
    }
}
