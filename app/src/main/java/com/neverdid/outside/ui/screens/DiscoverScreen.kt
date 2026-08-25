package com.neverdid.outside.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.ui.components.ActivityCard
import com.neverdid.outside.ui.components.OutsideAvatar
import com.neverdid.outside.ui.components.SectionTitle
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lime

@Composable
fun DiscoverScreen(
    activities: List<Activity>,
    joinedActivityIds: List<String>,
    locationName: String,
    profileInitials: String,
    innerPadding: PaddingValues,
    onActivityClick: (Activity) -> Unit,
    onHostActivity: () -> Unit,
    onProfileClick: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ActivityCategory.ALL) }

    val filteredActivities = activities.filter { activity ->
        (selectedCategory == ActivityCategory.ALL || activity.category == selectedCategory) &&
            (query.isBlank() || activity.title.contains(query, ignoreCase = true) ||
                activity.location.contains(query, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                DiscoverHeader(
                    locationName = locationName,
                    profileInitials = profileInitials,
                    onProfileClick = onProfileClick,
                )
                HeroCard(onHostActivity = onHostActivity)
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Activity or place") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ActivityCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text("${category.emoji} ${category.label}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Forest,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == category,
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent,
                        ),
                    )
                }
            }
        }

        item {
            SectionTitle(
                title = if (selectedCategory == ActivityCategory.ALL) "Happening nearby" else selectedCategory.label,
                modifier = Modifier.padding(horizontal = 20.dp),
                action = "${filteredActivities.size} plans",
                onAction = {},
            )
        }

        if (filteredActivities.isEmpty()) {
            item {
                EmptyDiscovery(onHostActivity = onHostActivity)
            }
        } else {
            items(filteredActivities, key = { it.id }) { activity ->
                ActivityCard(
                    activity = activity,
                    isJoined = activity.id in joinedActivityIds,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onClick = { onActivityClick(activity) },
                )
            }
        }
    }
}

@Composable
private fun DiscoverHeader(
    locationName: String,
    profileInitials: String,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = locationName.uppercase(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Go outside", style = MaterialTheme.typography.headlineLarge)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Change location",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications")
            }
            Spacer(Modifier.size(10.dp))
            OutsideAvatar(
                initials = profileInitials,
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onProfileClick),
            )
        }
    }
}

@Composable
private fun HeroCard(onHostActivity: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Forest),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "☀",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 14.dp),
                color = Lime.copy(alpha = 0.24f),
                style = MaterialTheme.typography.displayLarge,
            )
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Your next good story\nisn’t on the couch.",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    text = "18 people nearby are making plans today.",
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Button(
                    onClick = onHostActivity,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Lime,
                        contentColor = Forest,
                    ),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(7.dp))
                    Text("Start a plan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmptyDiscovery(onHostActivity: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("🌱", style = MaterialTheme.typography.displaySmall)
        Text("Nothing here yet", style = MaterialTheme.typography.titleLarge)
        Text(
            "Try another filter, or be the person who starts the plan.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = onHostActivity) { Text("Start a plan") }
    }
}
