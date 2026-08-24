package com.neverdid.outside.model

enum class ActivityCategory(
    val label: String,
    val emoji: String,
) {
    ALL("All", "✦"),
    HIKING("Hiking", "🥾"),
    RUNNING("Running", "🏃"),
    CYCLING("Cycling", "🚲"),
    CAMPING("Camping", "⛺"),
    CLIMBING("Climbing", "🧗"),
    CASUAL("Casual", "🌿"),
}

data class Activity(
    val id: String,
    val title: String,
    val category: ActivityCategory,
    val host: String,
    val hostInitials: String,
    val day: String,
    val date: String,
    val time: String,
    val location: String,
    val distance: String,
    val going: Int,
    val capacity: Int,
    val vibe: String,
    val description: String,
    val bring: List<String>,
    val accent: ActivityAccent,
)

enum class ActivityAccent {
    FOREST,
    SUNSET,
    LAKE,
    VIOLET,
}

data class FeedPost(
    val id: String,
    val author: String,
    val initials: String,
    val timeAgo: String,
    val text: String,
    val activityLabel: String,
    val reactions: Int,
    val comments: Int,
    val accent: ActivityAccent,
)

data class ForumTopic(
    val id: String,
    val title: String,
    val category: String,
    val author: String,
    val preview: String,
    val replies: Int,
    val timeAgo: String,
    val isHot: Boolean = false,
)

data class Conversation(
    val id: String,
    val name: String,
    val initials: String,
    val preview: String,
    val time: String,
    val unread: Int = 0,
    val isGroup: Boolean = false,
    val activity: String? = null,
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val body: String,
    val time: String,
    val isMine: Boolean,
)
