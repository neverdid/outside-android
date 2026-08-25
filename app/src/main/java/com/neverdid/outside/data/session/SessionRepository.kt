package com.neverdid.outside.data.session

import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ExperienceLevel
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val currentProfile: Flow<UserProfile?>

    suspend fun signIn(email: String)

    suspend fun completeOnboarding(
        firstName: String,
        city: String,
        radiusKm: Int,
        interests: Set<ActivityCategory>,
        experience: ExperienceLevel,
    )

    suspend fun signOut()
}
