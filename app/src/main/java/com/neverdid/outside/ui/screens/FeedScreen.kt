package com.neverdid.outside.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neverdid.outside.feed.RankedFeedPost
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.model.FeedPostIntent
import com.neverdid.outside.ui.components.OutsideAvatar
import com.neverdid.outside.ui.components.accentColors
import com.neverdid.outside.ui.components.accentStrong
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lime

private enum class FeedFilter(val label: String) {
    FOR_YOU("For you"),
    NEARBY("Nearby"),
    FRESH("New"),
    FIND_PEOPLE("Find people"),
}

@Composable
fun FeedScreen(
    posts: List<RankedFeedPost>,
    likedPostIds: List<String>,
    firstName: String,
    innerPadding: PaddingValues,
    onLike: (String) -> Unit,
    onHide: (String) -> Unit,
    onOpenActivity: (String) -> Unit,
    onFindPlan: () -> Unit,
) {
    var selectedFilterName by rememberSaveable { mutableStateOf(FeedFilter.FOR_YOU.name) }
    val selectedFilter = FeedFilter.valueOf(selectedFilterName)
    val visiblePosts = remember(posts, selectedFilter) {
        when (selectedFilter) {
            FeedFilter.FOR_YOU -> posts
            FeedFilter.NEARBY -> posts
                .filter { it.post.distanceKm != null }
                .sortedBy { it.post.distanceKm }
            FeedFilter.FRESH -> posts.sortedByDescending { it.post.createdAtEpochMillis }
            FeedFilter.FIND_PEOPLE -> posts.filter {
                it.post.intent == FeedPostIntent.LOOKING_FOR_PEOPLE
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("Outside lately", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Picked for ${firstName.ifBlank { "you" }} to spark a real plan, not just a scroll.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(FeedFilter.entries, key = { it.name }) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilterName = filter.name },
                        label = { Text(filter.label) },
                    )
                }
            }
        }
        item { FeedPrompt(onFindPlan = onFindPlan) }
        if (visiblePosts.isEmpty()) {
            item {
                EmptyFeed(
                    isFiltered = posts.isNotEmpty(),
                    onShowAll = { selectedFilterName = FeedFilter.FOR_YOU.name },
                    onFindPlan = onFindPlan,
                )
            }
        } else {
            items(visiblePosts, key = { it.post.id }) { rankedPost ->
                PostCard(
                    rankedPost = rankedPost,
                    isLiked = rankedPost.post.id in likedPostIds,
                    onLike = { onLike(rankedPost.post.id) },
                    onHide = { onHide(rankedPost.post.id) },
                    onOpenActivity = rankedPost.post.relatedActivityId?.let { activityId ->
                        { onOpenActivity(activityId) }
                    },
                )
            }
            item { CaughtUpCard(onFindPlan = onFindPlan) }
        }
    }
}

@Composable
private fun FeedPrompt(onFindPlan: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Lime),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Forest),
                contentAlignment = Alignment.Center,
            ) {
                Text("↗", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Make the next post yours", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Find something happening near you.",
                    color = Forest.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Button(
                onClick = onFindPlan,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
            ) { Text("Explore") }
        }
    }
}

@Composable
private fun PostCard(
    rankedPost: RankedFeedPost,
    isLiked: Boolean,
    onLike: () -> Unit,
    onHide: () -> Unit,
    onOpenActivity: (() -> Unit)?,
) {
    val post = rankedPost.post
    val colors = accentColors(post.accent)
    val strong = accentStrong(post.accent)
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutsideAvatar(
                    initials = post.initials,
                    modifier = Modifier.size(42.dp),
                    background = colors.first,
                    foreground = Forest,
                )
                Spacer(Modifier.size(11.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.author, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${post.category.emoji} ${post.category.label} · ${post.timeAgo}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "Post options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Show me less like this") },
                            leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onHide()
                            },
                        )
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(188.dp)
                    .background(Brush.linearGradient(listOf(colors.first, colors.second))),
            ) {
                Text(
                    text = visualFor(post),
                    modifier = Modifier.align(Alignment.Center),
                    color = strong,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = post.activityLabel,
                    modifier = Modifier.align(Alignment.BottomStart).padding(15.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.90f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    color = Forest,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(100.dp),
                ) {
                    Text(
                        text = rankedPost.reason,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Text(post.text, style = MaterialTheme.typography.bodyLarge)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onLike, modifier = Modifier.size(38.dp)) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isLiked) "Remove reaction" else "React",
                                tint = if (isLiked) Color(0xFFE15050) else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = (post.reactions + if (isLiked) 1 else 0).toString(),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text("${post.comments} replies", style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (onOpenActivity != null) {
                        TextButton(onClick = onOpenActivity) { Text("View plan") }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFeed(
    isFiltered: Boolean,
    onShowAll: () -> Unit,
    onFindPlan: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 36.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(if (isFiltered) "Nothing in this view yet" else "Your feed is taking a breather")
        Text(
            if (isFiltered) {
                "Try your personalized feed while the community adds more posts."
            } else {
                "The best next step is finding a plan nearby."
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = if (isFiltered) onShowAll else onFindPlan) {
            Text(if (isFiltered) "Show for you" else "Explore plans")
        }
    }
}

@Composable
private fun CaughtUpCard(onFindPlan: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("You’re caught up 🌿", style = MaterialTheme.typography.titleMedium)
        Text(
            "Ready to turn inspiration into a plan?",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        TextButton(onClick = onFindPlan) { Text("Find something to do") }
    }
}

private fun visualFor(post: FeedPost): String = when (post.category) {
    ActivityCategory.HIKING -> "⌁  ⛰  ☀"
    ActivityCategory.RUNNING -> "◌  🏃  ↗"
    ActivityCategory.CYCLING -> "↝  🚲  ↝"
    ActivityCategory.CAMPING -> "✦  ⛺  ✦"
    ActivityCategory.CLIMBING -> "╱  🧗  ╱"
    ActivityCategory.CASUAL -> "≈  🌿  ≈"
    ActivityCategory.ALL -> "✦  ↗  ✦"
}
