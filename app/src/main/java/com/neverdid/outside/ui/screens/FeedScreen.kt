package com.neverdid.outside.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.ui.components.OutsideAvatar
import com.neverdid.outside.ui.components.accentColors
import com.neverdid.outside.ui.components.accentStrong
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lime

@Composable
fun FeedScreen(
    posts: List<FeedPost>,
    likedPostIds: List<String>,
    innerPadding: PaddingValues,
    onLike: (String) -> Unit,
    onFindPlan: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
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
                    "Small adventures from people near you.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        item {
            FeedPrompt(onFindPlan = onFindPlan)
        }
        items(posts, key = { it.id }) { post ->
            PostCard(
                post = post,
                isLiked = post.id in likedPostIds,
                onLike = { onLike(post.id) },
            )
        }
    }
}

@Composable
private fun FeedPrompt(onFindPlan: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Lime),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Forest),
                contentAlignment = Alignment.Center,
            ) {
                Text("↗", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Make the next post yours", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Find a plan happening near you.",
                    color = Forest.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Button(
                onClick = onFindPlan,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
            ) {
                Text("Explore")
            }
        }
    }
}

@Composable
private fun PostCard(
    post: FeedPost,
    isLiked: Boolean,
    onLike: () -> Unit,
) {
    val colors = accentColors(post.accent)
    val strong = accentStrong(post.accent)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
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
                        post.timeAgo,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "More")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(208.dp)
                    .background(Brush.linearGradient(listOf(colors.first, colors.second))),
            ) {
                Text(
                    text = when (post.accent.name) {
                        "FOREST" -> "⌁  ⛰  ☀"
                        "SUNSET" -> "◌  🏃  ↗"
                        "LAKE" -> "≈  🏄  ≈"
                        else -> "✦  ⛺  ✦"
                    },
                    modifier = Modifier.align(Alignment.Center),
                    color = strong,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = post.activityLabel,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.88f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    color = Forest,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(post.text, style = MaterialTheme.typography.bodyLarge)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onLike, modifier = Modifier.size(38.dp)) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
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
                            contentDescription = "Comments",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(post.comments.toString(), style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Save",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
