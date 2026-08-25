package com.neverdid.outside.data.content

import com.neverdid.outside.data.SampleData
import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.ActivityAccent
import com.neverdid.outside.model.ChatMessage
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.model.ForumTopic
import com.neverdid.outside.model.UserProfile
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DemoActivityRepository : ActivityRepository {
    private val activityState = MutableStateFlow(SampleData.activities)
    private val joinedState = MutableStateFlow<Set<String>>(emptySet())

    override val activities: Flow<List<Activity>> = activityState
    override val joinedActivityIds: Flow<Set<String>> = joinedState

    override suspend fun toggleJoin(activityId: String) {
        val isJoined = activityId in joinedState.value
        joinedState.update { joined ->
            if (isJoined) joined - activityId else joined + activityId
        }
        activityState.update { activities ->
            activities.map { activity ->
                if (activity.id != activityId) activity else activity.copy(
                    going = if (isJoined) {
                        (activity.going - 1).coerceAtLeast(0)
                    } else {
                        (activity.going + 1).coerceAtMost(activity.capacity)
                    },
                )
            }
        }
    }

    override suspend fun createActivity(draft: NewActivity, host: UserProfile) {
        val date = LocalDate.now().plusDays(1)
        val activity = Activity(
            id = "demo-${System.currentTimeMillis()}",
            title = draft.title.trim(),
            category = draft.category,
            host = host.firstName,
            hostInitials = host.initials,
            day = "NEW",
            date = date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)).uppercase(),
            time = "TBD",
            location = draft.location.trim(),
            distance = "Near ${host.city}",
            going = 1,
            capacity = 8,
            vibe = "Friendly pace · Newcomers welcome",
            description = "A new plan hosted by ${host.firstName}. Message the host to coordinate the details.",
            bring = listOf("Water", "Weather-ready layers"),
            accent = ActivityAccent.entries[draft.category.ordinal % ActivityAccent.entries.size],
        )
        activityState.update { listOf(activity) + it }
        joinedState.update { it + activity.id }
    }
}

class DemoFeedRepository : FeedRepository {
    private val postState = MutableStateFlow(SampleData.feedPosts)
    private val likedState = MutableStateFlow<Set<String>>(emptySet())

    override val posts: Flow<List<FeedPost>> = postState
    override val likedPostIds: Flow<Set<String>> = likedState

    override suspend fun toggleLike(postId: String) {
        likedState.update { liked ->
            if (postId in liked) liked - postId else liked + postId
        }
    }
}

class DemoCommunityRepository : CommunityRepository {
    private val topicState = MutableStateFlow(SampleData.topics)
    override val topics: Flow<List<ForumTopic>> = topicState

    override suspend fun createTopic(
        title: String,
        body: String,
        category: String,
        author: UserProfile,
    ) {
        topicState.update { topics ->
            listOf(
                ForumTopic(
                    id = "demo-${System.currentTimeMillis()}",
                    title = title.trim(),
                    category = category.uppercase(),
                    author = author.firstName,
                    preview = body.trim(),
                    replies = 0,
                    timeAgo = "Now",
                ),
            ) + topics
        }
    }
}

class DemoConversationRepository : ConversationRepository {
    private val conversationState = MutableStateFlow(SampleData.conversations)
    private val messageState = MutableStateFlow(SampleData.messages)

    override val conversations: Flow<List<Conversation>> = conversationState

    override fun messages(conversationId: String): Flow<List<ChatMessage>> =
        messageState.map { messages -> messages[conversationId].orEmpty() }

    override suspend fun sendMessage(
        conversationId: String,
        body: String,
        sender: UserProfile,
    ) {
        val trimmedBody = body.trim()
        if (trimmedBody.isEmpty()) return
        val message = ChatMessage(
            id = "demo-${System.currentTimeMillis()}",
            sender = sender.firstName,
            body = trimmedBody,
            time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
            isMine = true,
        )
        messageState.update { messages ->
            messages + (conversationId to (messages[conversationId].orEmpty() + message))
        }
        conversationState.update { conversations ->
            conversations.map { conversation ->
                if (conversation.id == conversationId) {
                    conversation.copy(preview = trimmedBody, time = "Now")
                } else {
                    conversation
                }
            }
        }
    }
}

fun demoContentRepositories(): ContentRepositories = ContentRepositories(
    activities = DemoActivityRepository(),
    feed = DemoFeedRepository(),
    community = DemoCommunityRepository(),
    conversations = DemoConversationRepository(),
)
