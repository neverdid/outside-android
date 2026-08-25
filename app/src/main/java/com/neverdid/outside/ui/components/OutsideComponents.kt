package com.neverdid.outside.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.ActivityAccent
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lake
import com.neverdid.outside.ui.theme.LakeSoft
import com.neverdid.outside.ui.theme.Lime
import com.neverdid.outside.ui.theme.LimeSoft
import com.neverdid.outside.ui.theme.Sunset
import com.neverdid.outside.ui.theme.SunsetSoft
import com.neverdid.outside.ui.theme.Violet
import com.neverdid.outside.ui.theme.VioletSoft

@Composable
fun OutsideAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.primaryContainer,
    foreground: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = foreground,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        if (action != null && onAction != null) {
            Text(
                text = action,
                modifier = Modifier.clickable(onClick = onAction).padding(8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun ActivityCard(
    activity: Activity,
    isJoined: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = accentColors(activity.accent)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(colors.first, colors.second),
                    ),
                ),
        ) {
            Text(
                text = activity.category.emoji,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 28.dp),
                fontSize = 64.sp,
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = activity.day,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.86f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    color = Forest,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = activity.date,
                    color = Forest.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            if (isJoined) {
                Text(
                    text = "YOU’RE IN",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Forest)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    color = Lime,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = activity.vibe,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Metadata(icon = Icons.Default.CalendarMonth, text = activity.time)
                Metadata(icon = Icons.Default.LocationOn, text = activity.distance)
                Metadata(
                    icon = Icons.Default.PeopleAlt,
                    text = "${activity.going}/${activity.capacity}",
                )
            }
        }
    }
}

@Composable
private fun Metadata(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

fun accentColors(accent: ActivityAccent): Pair<Color, Color> = when (accent) {
    ActivityAccent.FOREST -> LimeSoft to Color(0xFFBBD8A8)
    ActivityAccent.SUNSET -> SunsetSoft to Color(0xFFFFB08D)
    ActivityAccent.LAKE -> LakeSoft to Color(0xFF93CDD5)
    ActivityAccent.VIOLET -> VioletSoft to Color(0xFFC4B4E4)
}

fun accentStrong(accent: ActivityAccent): Color = when (accent) {
    ActivityAccent.FOREST -> Forest
    ActivityAccent.SUNSET -> Sunset
    ActivityAccent.LAKE -> Lake
    ActivityAccent.VIOLET -> Violet
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostActivitySheet(
    onDismiss: () -> Unit,
    onPublish: (String, String, ActivityCategory) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ActivityCategory.HIKING) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Start a plan", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Keep it simple. People are more likely to join a clear, friendly plan.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What are you doing?") },
                placeholder = { Text("Easy forest walk") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Where?") },
                placeholder = { Text("Meeting point") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
            )
            Text("Activity", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ActivityCategory.entries.filterNot { it == ActivityCategory.ALL }.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text("${category.emoji} ${category.label}") },
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Button(
                onClick = {
                    onPublish(
                        title.ifBlank { "Untitled outside plan" },
                        location,
                        selectedCategory,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = title.isNotBlank() && location.isNotBlank(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Forest,
                    contentColor = Color.White,
                ),
            ) {
                Text("Save draft plan")
            }
        }
    }
}
