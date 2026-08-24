package com.neverdid.outside.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neverdid.outside.model.Activity
import com.neverdid.outside.ui.components.OutsideAvatar
import com.neverdid.outside.ui.components.accentColors
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lime

@Composable
fun ActivityDetailScreen(
    activity: Activity,
    isJoined: Boolean,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onJoin: () -> Unit,
    onMessageHost: () -> Unit,
) {
    var isSaved by remember(activity.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 18.dp),
        ) {
            item {
                ActivityHero(
                    activity = activity,
                    isSaved = isSaved,
                    onBack = onBack,
                    onSave = { isSaved = !isSaved },
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${activity.category.emoji}  ${activity.category.label.uppercase()}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                        )
                        Text(activity.title, style = MaterialTheme.typography.headlineLarge)
                        Text(
                            activity.vibe,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Logistics(activity)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    HostCard(activity = activity, onMessageHost = onMessageHost)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("The plan", style = MaterialTheme.typography.titleLarge)
                        Text(activity.description, style = MaterialTheme.typography.bodyLarge)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Bring along", style = MaterialTheme.typography.titleLarge)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            activity.bring.forEach { item ->
                                Text(
                                    text = item,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 11.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                    PeopleGoing(activity)
                    SafetyNote()
                }
            }
        }
        JoinBar(
            activity = activity,
            isJoined = isJoined,
            onJoin = onJoin,
        )
    }
}

@Composable
private fun ActivityHero(
    activity: Activity,
    isSaved: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    val colors = accentColors(activity.accent)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(284.dp)
            .background(Brush.linearGradient(listOf(colors.first, colors.second))),
    ) {
        Text(
            text = activity.category.emoji,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 88.sp,
        )
        Text(
            text = "${activity.day} · ${activity.time}",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            color = Forest,
            style = MaterialTheme.typography.labelMedium,
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                description = "Back",
                onClick = onBack,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CircleIconButton(icon = Icons.Default.Share, description = "Share", onClick = {})
                CircleIconButton(
                    icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    description = "Save",
                    onClick = onSave,
                )
            }
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.9f)),
    ) {
        Icon(icon, contentDescription = description, tint = Forest)
    }
}

@Composable
private fun Logistics(activity: Activity) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            DetailRow(
                icon = Icons.Default.CalendarMonth,
                title = "${activity.day.lowercase().replaceFirstChar { it.uppercase() }}, ${activity.date}",
                subtitle = activity.time,
            )
            DetailRow(
                icon = Icons.Default.LocationOn,
                title = activity.location,
                subtitle = activity.distance,
            )
            DetailRow(
                icon = Icons.Default.PeopleAlt,
                title = "${activity.going} people going",
                subtitle = "${activity.capacity - activity.going} spots left",
            )
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Forest)
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun HostCard(activity: Activity, onMessageHost: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutsideAvatar(
            initials = activity.hostInitials,
            modifier = Modifier.size(52.dp),
            background = Lime,
            foreground = Forest,
        )
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Hosted by ${activity.host}", style = MaterialTheme.typography.titleMedium)
            Text(
                "12 plans hosted · 4.9 ★",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        OutlinedButton(
            onClick = onMessageHost,
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp),
        ) {
            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(17.dp))
            Spacer(Modifier.size(5.dp))
            Text("DM")
        }
    }
}

@Composable
private fun PeopleGoing(activity: Activity) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Who’s going", style = MaterialTheme.typography.titleLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            listOf("AN", "SM", "RV", "EC").take(activity.going.coerceAtMost(4)).forEachIndexed { index, initials ->
                OutsideAvatar(
                    initials = initials,
                    modifier = Modifier
                        .padding(start = if (index == 0) 0.dp else 0.dp)
                        .size(39.dp),
                    background = if (index % 2 == 0) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    foreground = Forest,
                )
                Spacer(Modifier.size(5.dp))
            }
            if (activity.going > 4) {
                Text(
                    "+${activity.going - 4} more",
                    modifier = Modifier.padding(start = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun SafetyNote() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("A small safety habit", style = MaterialTheme.typography.titleMedium)
            Text(
                "Meet in a public place, tell someone your plan, and trust your instincts.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun JoinBar(activity: Activity, isJoined: Boolean, onJoin: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isJoined) "You’re going" else "${activity.capacity - activity.going} spots left",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (isJoined) "Group chat unlocked" else "Free · RSVP required",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Button(
                onClick = onJoin,
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isJoined) MaterialTheme.colorScheme.surfaceVariant else Forest,
                    contentColor = if (isJoined) Forest else Color.White,
                ),
            ) {
                Text(if (isJoined) "Leave plan" else "Join the plan", fontWeight = FontWeight.Bold)
            }
        }
    }
}
