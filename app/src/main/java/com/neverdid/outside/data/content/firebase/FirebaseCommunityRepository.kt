package com.neverdid.outside.data.content.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.neverdid.outside.data.content.CommunityRepository
import com.neverdid.outside.model.ForumTopic
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseCommunityRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : CommunityRepository {
    override val topics: Flow<List<ForumTopic>> = firestore.collection(TOPICS)
        .limit(50)
        .documentSnapshots()
        .map { snapshots -> snapshots.mapNotNull { it.toForumTopic() } }

    override suspend fun createTopic(
        title: String,
        body: String,
        category: String,
        author: UserProfile,
    ) {
        val userId = checkNotNull(auth.currentUser?.uid) { "Sign in to start a topic." }
        firestore.collection(TOPICS).add(
            mapOf(
                "title" to title.trim(),
                "body" to body.trim(),
                "category" to category.uppercase(),
                "authorId" to userId,
                "authorName" to author.firstName,
                "replies" to 0,
                "isHot" to false,
                "status" to "active",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
    }

    private companion object {
        const val TOPICS = "forumTopics"
    }
}
