package com.neverdid.outside.data.content.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.neverdid.outside.data.content.FeedRepository
import com.neverdid.outside.model.FeedPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await

class FirebaseFeedRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    scope: CoroutineScope,
) : FeedRepository {
    private val documents = firestore.collection(FEED_POSTS)
        .limit(50)
        .documentSnapshots()
        .shareIn(scope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    override val posts: Flow<List<FeedPost>> = documents.map { snapshots ->
        val userId = auth.currentUser?.uid.orEmpty()
        snapshots.mapNotNull { snapshot ->
            snapshot.toFeedPost()?.let { post ->
                if (snapshot.containsUser("likedBy", userId)) {
                    post.copy(reactions = (post.reactions - 1).coerceAtLeast(0))
                } else {
                    post
                }
            }
        }
    }

    override val likedPostIds: Flow<Set<String>> = documents.map { snapshots ->
        val userId = auth.currentUser?.uid.orEmpty()
        snapshots.filter { it.containsUser("likedBy", userId) }.mapTo(mutableSetOf()) { it.id }
    }

    override suspend fun toggleLike(postId: String) {
        val userId = checkNotNull(auth.currentUser?.uid) { "Sign in to react to a post." }
        val reference = firestore.collection(FEED_POSTS).document(postId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(reference)
            val likedBy = snapshot.get("likedBy").stringList()
            val reactions = snapshot.getLong("reactions")?.toInt() ?: likedBy.size
            val isLiked = userId in likedBy
            transaction.update(
                reference,
                mapOf(
                    "likedBy" to if (isLiked) likedBy - userId else likedBy + userId,
                    "reactions" to if (isLiked) {
                        (reactions - 1).coerceAtLeast(0)
                    } else {
                        reactions + 1
                    },
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
            )
        }.await()
    }

    private companion object {
        const val FEED_POSTS = "feedPosts"
    }
}
