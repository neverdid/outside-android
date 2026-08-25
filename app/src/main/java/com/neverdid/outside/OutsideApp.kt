package com.neverdid.outside

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neverdid.outside.content.OutsideViewModel
import com.neverdid.outside.content.OutsideViewModelFactory
import com.neverdid.outside.data.BackendMode
import com.neverdid.outside.data.content.ContentRepositories
import com.neverdid.outside.data.content.NewActivity
import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.model.UserProfile
import com.neverdid.outside.ui.components.HostActivitySheet
import com.neverdid.outside.ui.components.TopicComposerSheet
import com.neverdid.outside.ui.screens.ActivityDetailScreen
import com.neverdid.outside.ui.screens.ChatScreen
import com.neverdid.outside.ui.screens.DiscoverScreen
import com.neverdid.outside.ui.screens.FeedScreen
import com.neverdid.outside.ui.screens.ForumScreen
import com.neverdid.outside.ui.screens.InboxScreen
import com.neverdid.outside.ui.screens.ProfileScreen
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lime
import kotlinx.coroutines.launch

private enum class AppTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    DISCOVER("Discover", Icons.Filled.Explore, Icons.Outlined.Explore),
    FEED("Feed", Icons.Filled.DynamicFeed, Icons.Outlined.DynamicFeed),
    COMMUNITY("Community", Icons.Filled.Forum, Icons.Outlined.Forum),
    INBOX("Inbox", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline),
}

@Composable
fun OutsideApp(
    profile: UserProfile,
    backendMode: BackendMode,
    repositories: ContentRepositories,
    onSignOut: () -> Unit,
) {
    val viewModelFactory = remember(repositories) { OutsideViewModelFactory(repositories) }
    val outsideViewModel: OutsideViewModel = viewModel(
        key = "outside-${profile.id}",
        factory = viewModelFactory,
    )
    val uiState by outsideViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(AppTab.DISCOVER) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    var showProfile by remember { mutableStateOf(false) }
    var showHostSheet by remember { mutableStateOf(false) }
    var showTopicSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(outsideViewModel) {
        outsideViewModel.events.collect { message -> snackbarHostState.showSnackbar(message) }
    }

    val onBack: () -> Unit = {
        selectedActivity = null
        selectedConversation = null
        showProfile = false
    }
    BackHandler(
        enabled = selectedActivity != null || selectedConversation != null || showProfile,
        onBack = onBack,
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (selectedActivity == null && selectedConversation == null && !showProfile) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    AppTab.entries.forEach { tab ->
                        val selected = selectedTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Forest,
                                selectedTextColor = Forest,
                                indicatorColor = Lime,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        when {
            showProfile -> ProfileScreen(
                profile = profile,
                backendMode = backendMode,
                innerPadding = innerPadding,
                onBack = onBack,
                onSignOut = onSignOut,
            )

            selectedActivity != null -> {
                val currentActivity = uiState.activities.firstOrNull { it.id == selectedActivity!!.id }
                    ?: selectedActivity!!
                ActivityDetailScreen(
                    activity = currentActivity,
                    isJoined = currentActivity.id in uiState.joinedActivityIds,
                    innerPadding = innerPadding,
                    onBack = onBack,
                    onJoin = {
                        val wasJoined = currentActivity.id in uiState.joinedActivityIds
                        outsideViewModel.toggleJoin(currentActivity.id)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (wasJoined) {
                                    "You left this plan"
                                } else {
                                    "You’re in! The group chat is ready."
                                },
                            )
                        }
                    },
                    onMessageHost = {
                        val hostConversation = uiState.conversations.firstOrNull {
                            it.name.contains(currentActivity.host.substringBefore(" "))
                        }
                        if (hostConversation != null) {
                            selectedConversation = hostConversation
                            selectedActivity = null
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "A direct conversation with this host isn’t available yet.",
                                )
                            }
                        }
                    },
                )
            }

            selectedConversation != null -> ConnectedChatScreen(
                conversation = selectedConversation!!,
                profile = profile,
                outsideViewModel = outsideViewModel,
                innerPadding = innerPadding,
                onBack = onBack,
            )

            selectedTab == AppTab.DISCOVER -> DiscoverScreen(
                activities = uiState.activities,
                joinedActivityIds = uiState.joinedActivityIds.toList(),
                locationName = profile.city,
                profileInitials = profile.initials,
                innerPadding = innerPadding,
                onActivityClick = { selectedActivity = it },
                onHostActivity = { showHostSheet = true },
                onProfileClick = { showProfile = true },
            )

            selectedTab == AppTab.FEED -> FeedScreen(
                posts = uiState.posts,
                likedPostIds = uiState.likedPostIds.toList(),
                innerPadding = innerPadding,
                onLike = outsideViewModel::toggleLike,
                onFindPlan = { selectedTab = AppTab.DISCOVER },
            )

            selectedTab == AppTab.COMMUNITY -> ForumScreen(
                topics = uiState.topics,
                innerPadding = innerPadding,
                onNewTopic = { showTopicSheet = true },
            )

            else -> InboxScreen(
                conversations = uiState.conversations,
                innerPadding = innerPadding,
                onConversationClick = { selectedConversation = it },
            )
        }
    }

    if (showHostSheet) {
        HostActivitySheet(
            onDismiss = { showHostSheet = false },
            onPublish = { title, location, category ->
                showHostSheet = false
                outsideViewModel.createActivity(
                    draft = NewActivity(title, location, category),
                    host = profile,
                )
            },
        )
    }

    if (showTopicSheet) {
        TopicComposerSheet(
            onDismiss = { showTopicSheet = false },
            onPublish = { title, body, category ->
                showTopicSheet = false
                outsideViewModel.createTopic(title, body, category, profile)
            },
        )
    }
}

@Composable
private fun ConnectedChatScreen(
    conversation: Conversation,
    profile: UserProfile,
    outsideViewModel: OutsideViewModel,
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onBack: () -> Unit,
) {
    val messageFlow = remember(conversation.id) { outsideViewModel.messages(conversation.id) }
    val messages by messageFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    ChatScreen(
        conversation = conversation,
        messages = messages,
        innerPadding = innerPadding,
        onBack = onBack,
        onSendMessage = { body -> outsideViewModel.sendMessage(conversation.id, body, profile) },
    )
}
