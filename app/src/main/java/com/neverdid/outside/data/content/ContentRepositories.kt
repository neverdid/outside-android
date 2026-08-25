package com.neverdid.outside.data.content

import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ChatMessage
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.model.ForumTopic
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.flow.Flow

data class NewActivity(
    val title: String,
    val location: String,
    val category: ActivityCategory,
)

interface ActivityRepository {
    val activities: Flow<List<Activity>>
    val joinedActivityIds: Flow<Set<String>>

    suspend fun toggleJoin(activityId: String)
    suspend fun createActivity(draft: NewActivity, host: UserProfile)
}

interface FeedRepository {
    val posts: Flow<List<FeedPost>>
    val likedPostIds: Flow<Set<String>>

    suspend fun toggleLike(postId: String)
}

interface CommunityRepository {
    val topics: Flow<List<ForumTopic>>

    suspend fun createTopic(
        title: String,
        body: String,
        category: String,
        author: UserProfile,
    )
}

interface ConversationRepository {
    val conversations: Flow<List<Conversation>>

    fun messages(conversationId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(conversationId: String, body: String, sender: UserProfile)
}

data class ContentRepositories(
    val activities: ActivityRepository,
    val feed: FeedRepository,
    val community: CommunityRepository,
    val conversations: ConversationRepository,
)
