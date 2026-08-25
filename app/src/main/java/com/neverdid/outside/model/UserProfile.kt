package com.neverdid.outside.model

enum class ExperienceLevel(
    val label: String,
    val description: String,
) {
    NEW("Just starting", "I would like patient, beginner-friendly plans."),
    CASUAL("Casual", "I get outside sometimes and prefer a relaxed pace."),
    EXPERIENCED("Experienced", "I am comfortable helping a group prepare."),
}

data class UserProfile(
    val id: String,
    val email: String,
    val firstName: String = "",
    val city: String = "",
    val radiusKm: Int = 20,
    val interests: Set<ActivityCategory> = emptySet(),
    val experience: ExperienceLevel? = null,
    val onboardingComplete: Boolean = false,
) {
    val initials: String
        get() = firstName
            .trim()
            .split(Regex("\\s+"))
            .filter(String::isNotBlank)
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { email.firstOrNull()?.uppercase() ?: "?" }
}
