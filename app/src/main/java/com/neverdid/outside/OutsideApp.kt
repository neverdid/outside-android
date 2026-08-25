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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.neverdid.outside.data.SampleData
import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.model.UserProfile
import com.neverdid.outside.ui.components.HostActivitySheet
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
    onSignOut: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(AppTab.DISCOVER) }
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    var showProfile by remember { mutableStateOf(false) }
    var showHostSheet by remember { mutableStateOf(false) }
    val joinedActivityIds = remember { mutableStateListOf<String>() }
    val likedPostIds = remember { mutableStateListOf<String>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                innerPadding = innerPadding,
                onBack = onBack,
                onSignOut = onSignOut,
            )

            selectedActivity != null -> ActivityDetailScreen(
                activity = selectedActivity!!,
                isJoined = selectedActivity!!.id in joinedActivityIds,
                innerPadding = innerPadding,
                onBack = onBack,
                onJoin = {
                    val id = selectedActivity!!.id
                    if (id in joinedActivityIds) {
                        joinedActivityIds.remove(id)
                        scope.launch { snackbarHostState.showSnackbar("You left this plan") }
                    } else {
                        joinedActivityIds.add(id)
                        scope.launch { snackbarHostState.showSnackbar("You’re in! The group chat is ready.") }
                    }
                },
                onMessageHost = {
                    selectedConversation = SampleData.conversations.firstOrNull {
                        it.name.contains(selectedActivity!!.host.substringBefore(" "))
                    } ?: SampleData.conversations.first()
                    selectedActivity = null
                },
            )

            selectedConversation != null -> ChatScreen(
                conversation = selectedConversation!!,
                initialMessages = SampleData.messages[selectedConversation!!.id].orEmpty(),
                innerPadding = innerPadding,
                onBack = onBack,
            )

            selectedTab == AppTab.DISCOVER -> DiscoverScreen(
                activities = SampleData.activities,
                joinedActivityIds = joinedActivityIds,
                locationName = profile.city,
                profileInitials = profile.initials,
                innerPadding = innerPadding,
                onActivityClick = { selectedActivity = it },
                onHostActivity = { showHostSheet = true },
                onProfileClick = { showProfile = true },
            )

            selectedTab == AppTab.FEED -> FeedScreen(
                posts = SampleData.feedPosts,
                likedPostIds = likedPostIds,
                innerPadding = innerPadding,
                onLike = { id ->
                    if (id in likedPostIds) likedPostIds.remove(id) else likedPostIds.add(id)
                },
                onFindPlan = { selectedTab = AppTab.DISCOVER },
            )

            selectedTab == AppTab.COMMUNITY -> ForumScreen(
                topics = SampleData.topics,
                innerPadding = innerPadding,
                onNewTopic = {
                    scope.launch { snackbarHostState.showSnackbar("Topic composer is ready for backend wiring") }
                },
            )

            else -> InboxScreen(
                conversations = SampleData.conversations,
                innerPadding = innerPadding,
                onConversationClick = { selectedConversation = it },
            )
        }
    }

    if (showHostSheet) {
        HostActivitySheet(
            onDismiss = { showHostSheet = false },
            onPublish = { title ->
                showHostSheet = false
                scope.launch {
                    snackbarHostState.showSnackbar("“$title” is saved as a draft plan")
                }
            },
        )
    }
}
