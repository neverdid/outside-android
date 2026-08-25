package com.neverdid.outside.data.content.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.neverdid.outside.data.content.ConversationRepository
import com.neverdid.outside.model.ChatMessage
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseConversationRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ConversationRepository {
    override val conversations: Flow<List<Conversation>>
        get() {
            val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
            return firestore.collection(CONVERSATIONS)
                .whereArrayContains("memberIds", userId)
                .limit(50)
                .documentSnapshots()
                .map { snapshots -> snapshots.mapNotNull { it.toConversation() } }
        }

    override fun messages(conversationId: String): Flow<List<ChatMessage>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return firestore.collection(CONVERSATIONS).document(conversationId)
            .collection(MESSAGES)
            .orderBy("sentAt", Query.Direction.ASCENDING)
            .limitToLast(200)
            .documentSnapshots()
            .map { snapshots -> snapshots.mapNotNull { it.toChatMessage(userId) } }
    }

    override suspend fun sendMessage(
        conversationId: String,
        body: String,
        sender: UserProfile,
    ) {
        val userId = checkNotNull(auth.currentUser?.uid) { "Sign in to send a message." }
        val trimmedBody = body.trim()
        if (trimmedBody.isEmpty()) return
        val conversation = firestore.collection(CONVERSATIONS).document(conversationId)
        val message = conversation.collection(MESSAGES).document()
        firestore.runBatch { batch ->
            batch.set(
                message,
                mapOf(
                    "senderId" to userId,
                    "senderName" to sender.firstName,
                    "body" to trimmedBody,
                    "sentAt" to FieldValue.serverTimestamp(),
                    "moderationState" to "pending",
                ),
            )
            batch.update(
                conversation,
                mapOf(
                    "lastMessage" to trimmedBody,
                    "lastMessageAt" to FieldValue.serverTimestamp(),
                    "lastSenderId" to userId,
                ),
            )
        }.await()
    }

    private companion object {
        const val CONVERSATIONS = "conversations"
        const val MESSAGES = "messages"
    }
}
