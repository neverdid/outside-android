package com.neverdid.outside.data.content.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.ActivityAccent
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ChatMessage
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.model.ForumTopic
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun Query.documentSnapshots(): Flow<List<DocumentSnapshot>> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error)
        } else {
            trySend(snapshot?.documents.orEmpty())
        }
    }
    awaitClose { registration.remove() }
}

internal fun DocumentSnapshot.toActivity(): Activity? {
    val title = getString("title")?.takeIf { it.isNotBlank() } ?: return null
    return Activity(
        id = id,
        title = title,
        category = enumValue(getString("category"), ActivityCategory.HIKING),
        host = getString("hostName").orEmpty().ifBlank { "Outside host" },
        hostInitials = getString("hostInitials").orEmpty().ifBlank { "OH" },
        day = getString("day").orEmpty().ifBlank { "UPCOMING" },
        date = getString("date").orEmpty().ifBlank { "DATE TBD" },
        time = getString("time").orEmpty().ifBlank { "TBD" },
        location = getString("location").orEmpty().ifBlank { "Meeting point TBD" },
        distance = getString("distance").orEmpty().ifBlank { "Nearby" },
        going = getLong("going")?.toInt() ?: 1,
        capacity = getLong("capacity")?.toInt() ?: 8,
        vibe = getString("vibe").orEmpty().ifBlank { "Friendly pace" },
        description = getString("description").orEmpty(),
        bring = get("bring").stringList(),
        accent = enumValue(getString("accent"), ActivityAccent.FOREST),
    )
}

internal fun DocumentSnapshot.toFeedPost(): FeedPost? {
    val text = getString("text")?.takeIf { it.isNotBlank() } ?: return null
    return FeedPost(
        id = id,
        author = getString("authorName").orEmpty().ifBlank { "Outside member" },
        initials = getString("authorInitials").orEmpty().ifBlank { "O" },
        timeAgo = displayTime(getTimestamp("createdAt"), getString("timeAgo")),
        text = text,
        activityLabel = getString("activityLabel").orEmpty(),
        reactions = getLong("reactions")?.toInt() ?: 0,
        comments = getLong("comments")?.toInt() ?: 0,
        accent = enumValue(getString("accent"), ActivityAccent.FOREST),
    )
}

internal fun DocumentSnapshot.toForumTopic(): ForumTopic? {
    val title = getString("title")?.takeIf { it.isNotBlank() } ?: return null
    return ForumTopic(
        id = id,
        title = title,
        category = getString("category").orEmpty().ifBlank { "GENERAL" },
        author = getString("authorName").orEmpty().ifBlank { "Outside member" },
        preview = getString("body").orEmpty(),
        replies = getLong("replies")?.toInt() ?: 0,
        timeAgo = displayTime(getTimestamp("createdAt"), getString("timeAgo")),
        isHot = getBoolean("isHot") == true,
    )
}

internal fun DocumentSnapshot.toConversation(): Conversation? {
    val name = getString("name")?.takeIf { it.isNotBlank() } ?: return null
    return Conversation(
        id = id,
        name = name,
        initials = getString("initials").orEmpty().ifBlank { "O" },
        preview = getString("lastMessage").orEmpty(),
        time = displayTime(getTimestamp("lastMessageAt"), getString("time")),
        unread = 0,
        isGroup = getString("type") == "activity",
        activity = getString("activityLabel"),
    )
}

internal fun DocumentSnapshot.toChatMessage(currentUserId: String): ChatMessage? {
    val body = getString("body")?.takeIf { it.isNotBlank() } ?: return null
    return ChatMessage(
        id = id,
        sender = getString("senderName").orEmpty().ifBlank { "Outside member" },
        body = body,
        time = displayClockTime(getTimestamp("sentAt"), getString("time")),
        isMine = getString("senderId") == currentUserId,
    )
}

internal fun DocumentSnapshot.containsUser(field: String, userId: String): Boolean =
    get(field).stringList().contains(userId)

internal fun Any?.stringList(): List<String> =
    (this as? List<*>)?.mapNotNull { it as? String }.orEmpty()

private inline fun <reified T : Enum<T>> enumValue(value: String?, fallback: T): T =
    enumValues<T>().firstOrNull { it.name == value } ?: fallback

private fun displayTime(timestamp: Timestamp?, fallback: String?): String {
    if (timestamp == null) return fallback.orEmpty().ifBlank { "Recently" }
    val instant = timestamp.toDate().toInstant()
    val now = java.time.Instant.now()
    val minutes = java.time.Duration.between(instant, now).toMinutes().coerceAtLeast(0)
    return when {
        minutes < 1 -> "Now"
        minutes < 60 -> "$minutes min"
        minutes < 24 * 60 -> "${minutes / 60} hr"
        else -> "${minutes / (24 * 60)} d"
    }
}

private fun displayClockTime(timestamp: Timestamp?, fallback: String?): String =
    timestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())
        ?.format(DateTimeFormatter.ofPattern("HH:mm"))
        ?: fallback.orEmpty().ifBlank { "Now" }
