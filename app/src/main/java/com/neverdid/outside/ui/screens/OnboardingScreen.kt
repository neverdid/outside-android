package com.neverdid.outside.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ExperienceLevel
import com.neverdid.outside.model.UserProfile
import com.neverdid.outside.ui.onboarding.hasEnoughInterests
import com.neverdid.outside.ui.onboarding.isValidCity
import com.neverdid.outside.ui.onboarding.isValidFirstName
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lime

private const val ONBOARDING_STEP_COUNT = 4

@Composable
fun OnboardingScreen(
    profile: UserProfile,
    onCancel: () -> Unit,
    onComplete: (
        firstName: String,
        city: String,
        radiusKm: Int,
        interests: Set<ActivityCategory>,
        experience: ExperienceLevel,
    ) -> Unit,
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var firstName by rememberSaveable { mutableStateOf(profile.firstName) }
    var city by rememberSaveable { mutableStateOf(profile.city) }
    var radiusKm by rememberSaveable { mutableIntStateOf(profile.radiusKm) }
    var interests by remember { mutableStateOf(profile.interests) }
    var experience by remember { mutableStateOf(profile.experience) }
    var showValidation by rememberSaveable { mutableStateOf(false) }

    val canContinue = when (step) {
        0 -> isValidFirstName(firstName)
        1 -> isValidCity(city)
        2 -> hasEnoughInterests(interests)
        else -> experience != null
    }
    val goBack = {
        if (step > 0) {
            step -= 1
            showValidation = false
        } else {
            onCancel()
        }
    }

    BackHandler(onBack = goBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = goBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (step > 0) "Previous step" else "Back to sign in",
                    )
                }
                Text(
                    text = "${step + 1} of $ONBOARDING_STEP_COUNT",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            LinearProgressIndicator(
                progress = { (step + 1f) / ONBOARDING_STEP_COUNT },
                modifier = Modifier.fillMaxWidth(),
                color = Forest,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            when (step) {
                0 -> IdentityStep(
                    email = profile.email,
                    firstName = firstName,
                    onFirstNameChange = { firstName = it },
                    showValidation = showValidation,
                )

                1 -> LocationStep(
                    city = city,
                    onCityChange = { city = it },
                    radiusKm = radiusKm,
                    onRadiusChange = { radiusKm = it },
                    showValidation = showValidation,
                )

                2 -> InterestsStep(
                    interests = interests,
                    onToggle = { category ->
                        interests = if (category in interests) {
                            interests - category
                        } else {
                            interests + category
                        }
                    },
                    showValidation = showValidation,
                )

                else -> ExperienceStep(
                    selected = experience,
                    onSelect = { experience = it },
                    showValidation = showValidation,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Button(
                onClick = {
                    showValidation = true
                    if (canContinue) {
                        if (step < ONBOARDING_STEP_COUNT - 1) {
                            step += 1
                            showValidation = false
                        } else {
                            onComplete(
                                firstName.trim(),
                                city.trim(),
                                radiusKm,
                                interests,
                                checkNotNull(experience),
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
            ) {
                Text(
                    if (step == ONBOARDING_STEP_COUNT - 1) "Start exploring" else "Continue",
                    fontWeight = FontWeight.Bold,
                )
            }
            if (step > 0) {
                TextButton(onClick = goBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Back")
                }
            }
        }
    }
}

@Composable
private fun IdentityStep(
    email: String,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    showValidation: Boolean,
) {
    StepHeading(
        eyebrow = "LET’S MEET",
        title = "What should people call you?",
        body = "Use the name you feel comfortable sharing when you join a plan.",
    )
    Spacer(Modifier.height(30.dp))
    OutlinedTextField(
        value = firstName,
        onValueChange = onFirstNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("First name") },
        placeholder = { Text("Alex") },
        singleLine = true,
        isError = showValidation && !isValidFirstName(firstName),
        supportingText = {
            if (showValidation && !isValidFirstName(firstName)) Text("Enter at least 2 characters.")
        },
        shape = RoundedCornerShape(18.dp),
    )
    Text(
        text = email,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun LocationStep(
    city: String,
    onCityChange: (String) -> Unit,
    radiusKm: Int,
    onRadiusChange: (Int) -> Unit,
    showValidation: Boolean,
) {
    StepHeading(
        eyebrow = "YOUR AREA",
        title = "Where should we look?",
        body = "Tell us an approximate home area. Outside never needs your exact home address.",
    )
    Spacer(Modifier.height(28.dp))
    OutlinedTextField(
        value = city,
        onValueChange = onCityChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("City or area") },
        placeholder = { Text("Brașov") },
        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
        singleLine = true,
        isError = showValidation && !isValidCity(city),
        supportingText = {
            if (showValidation && !isValidCity(city)) Text("Enter a city or area.")
        },
        shape = RoundedCornerShape(18.dp),
    )
    Spacer(Modifier.height(24.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Discovery radius", style = MaterialTheme.typography.titleMedium)
        Text(
            "$radiusKm km",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
    Slider(
        value = radiusKm.toFloat(),
        onValueChange = { onRadiusChange(it.toInt()) },
        valueRange = 5f..50f,
        steps = 8,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("5 km", style = MaterialTheme.typography.bodySmall)
        Text("50 km", style = MaterialTheme.typography.bodySmall)
    }
    Spacer(Modifier.height(24.dp))
    PrivacyNote()
}

@Composable
private fun InterestsStep(
    interests: Set<ActivityCategory>,
    onToggle: (ActivityCategory) -> Unit,
    showValidation: Boolean,
) {
    StepHeading(
        eyebrow = "YOUR THINGS",
        title = "What gets you outside?",
        body = "Choose at least two. We’ll use these to make discovery feel relevant from day one.",
    )
    Spacer(Modifier.height(26.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ActivityCategory.entries.filterNot { it == ActivityCategory.ALL }.forEach { category ->
            FilterChip(
                selected = category in interests,
                onClick = { onToggle(category) },
                label = { Text("${category.emoji}  ${category.label}") },
                leadingIcon = if (category in interests) {
                    { Icon(Icons.Default.CheckCircle, contentDescription = null, Modifier.size(18.dp)) }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Forest,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Lime,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    }
    Spacer(Modifier.height(16.dp))
    Text(
        text = "${interests.size} selected",
        color = if (showValidation && !hasEnoughInterests(interests)) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        style = MaterialTheme.typography.bodyMedium,
    )
    if (showValidation && !hasEnoughInterests(interests)) {
        Text(
            "Choose at least two interests to continue.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ExperienceStep(
    selected: ExperienceLevel?,
    onSelect: (ExperienceLevel) -> Unit,
    showValidation: Boolean,
) {
    StepHeading(
        eyebrow = "YOUR PACE",
        title = "How do you like to join in?",
        body = "There is no best answer. This helps us make plans feel more comfortable and honest.",
    )
    Spacer(Modifier.height(24.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ExperienceLevel.entries.forEach { level ->
            val isSelected = selected == level
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(level) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Forest else MaterialTheme.colorScheme.surface,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            level.label,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            level.description,
                            color = if (isSelected) {
                                Color.White.copy(alpha = 0.72f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = Lime)
                    }
                }
            }
        }
    }
    if (showValidation && selected == null) {
        Text(
            "Choose the option that feels closest.",
            modifier = Modifier.padding(top = 12.dp),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun StepHeading(
    eyebrow: String,
    title: String,
    body: String,
) {
    Text(
        eyebrow,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(8.dp))
    Text(title, style = MaterialTheme.typography.displaySmall)
    Spacer(Modifier.height(10.dp))
    Text(
        body,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun PrivacyNote() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                Icons.Default.PrivacyTip,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column {
                Text("Approximate by design", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(3.dp))
                Text(
                    "Your profile shows an area, not a precise address. Exact meeting points belong to individual plans.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
