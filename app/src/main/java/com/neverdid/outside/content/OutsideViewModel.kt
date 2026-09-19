package com.neverdid.outside.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neverdid.outside.data.content.ContentRepositories
import com.neverdid.outside.data.content.NewActivity
import com.neverdid.outside.feed.FeedRanker
import com.neverdid.outside.feed.FeedRankingContext
import com.neverdid.outside.feed.RankedFeedPost
import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ChatMessage
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.model.ForumTopic
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OutsideUiState(
    val isLoading: Boolean = true,
    val activities: List<Activity> = emptyList(),
    val joinedActivityIds: Set<String> = emptySet(),
    val feed: List<RankedFeedPost> = emptyList(),
    val likedPostIds: Set<String> = emptySet(),
    val topics: List<ForumTopic> = emptyList(),
    val conversations: List<Conversation> = emptyList(),
)

private data class DiscoveryCommunityState(
    val activities: List<Activity>,
    val joinedActivityIds: Set<String>,
    val posts: List<FeedPost>,
    val likedPostIds: Set<String>,
    val topics: List<ForumTopic>,
)

private data class FeedSessionFeedback(
    val hiddenPostIds: Set<String> = emptySet(),
    val demotedCategories: Set<ActivityCategory> = emptySet(),
)

class OutsideViewModel(
    private val repositories: ContentRepositories,
    private val profile: UserProfile,
) : ViewModel() {
    private val feedFeedback = MutableStateFlow(FeedSessionFeedback())

    private val coreState = combine(
        repositories.activities.activities,
        repositories.activities.joinedActivityIds,
        repositories.feed.posts,
        repositories.feed.likedPostIds,
        repositories.community.topics,
    ) { activities, joinedIds, posts, likedIds, topics ->
        DiscoveryCommunityState(activities, joinedIds, posts, likedIds, topics)
    }

    val uiState: StateFlow<OutsideUiState> = combine(
        coreState,
        repositories.conversations.conversations,
        feedFeedback,
    ) { core, conversations, feedback ->
        val joinedCategories = core.activities
            .filter { it.id in core.joinedActivityIds }
            .mapTo(mutableSetOf()) { it.category }
        OutsideUiState(
            isLoading = false,
            activities = core.activities,
            joinedActivityIds = core.joinedActivityIds,
            feed = FeedRanker.rank(
                posts = core.posts,
                context = FeedRankingContext(
                    interests = profile.interests,
                    radiusKm = profile.radiusKm,
                    likedPostIds = core.likedPostIds,
                    joinedActivityIds = core.joinedActivityIds,
                    joinedCategories = joinedCategories,
                    hiddenPostIds = feedback.hiddenPostIds,
                    demotedCategories = feedback.demotedCategories,
                    sessionSeed = profile.id.hashCode(),
                ),
            ),
            likedPostIds = core.likedPostIds,
            topics = core.topics,
            conversations = conversations,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OutsideUiState(),
    )

    val events = MutableSharedFlow<String>(extraBufferCapacity = 4)

    fun messages(conversationId: String): Flow<List<ChatMessage>> =
        repositories.conversations.messages(conversationId)

    fun toggleJoin(activityId: String) = launchOperation {
        repositories.activities.toggleJoin(activityId)
    }

    fun createActivity(draft: NewActivity, host: UserProfile) = launchOperation(
        successMessage = "Your plan is live.",
    ) {
        repositories.activities.createActivity(draft, host)
    }

    fun toggleLike(postId: String) = launchOperation {
        repositories.feed.toggleLike(postId)
    }

    fun hidePost(postId: String) {
        val category = uiState.value.feed.firstOrNull { it.post.id == postId }?.post?.category
        feedFeedback.value = feedFeedback.value.copy(
            hiddenPostIds = feedFeedback.value.hiddenPostIds + postId,
            demotedCategories = if (category == null) {
                feedFeedback.value.demotedCategories
            } else {
                feedFeedback.value.demotedCategories + category
            },
        )
        events.tryEmit("Hidden for this session. We’ll tune your feed as you give more feedback.")
    }

    fun createTopic(
        title: String,
        body: String,
        category: String,
        author: UserProfile,
    ) = launchOperation(successMessage = "Your topic is live.") {
        repositories.community.createTopic(title, body, category, author)
    }

    fun sendMessage(conversationId: String, body: String, sender: UserProfile) = launchOperation {
        repositories.conversations.sendMessage(conversationId, body, sender)
    }

    private fun launchOperation(
        successMessage: String? = null,
        block: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess { successMessage?.let { events.emit(it) } }
                .onFailure { error ->
                    events.emit(error.message?.takeIf { it.isNotBlank() } ?: "That didn’t work. Try again.")
                }
        }
    }
}

class OutsideViewModelFactory(
    private val repositories: ContentRepositories,
    private val profile: UserProfile,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(OutsideViewModel::class.java))
        return OutsideViewModel(repositories, profile) as T
    }
}
