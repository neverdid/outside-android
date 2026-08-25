package com.neverdid.outside.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ExperienceLevel
import com.neverdid.outside.model.UserProfile
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.outsideSessionDataStore by preferencesDataStore(name = "outside_session")

class LocalSessionRepository(context: Context) : SessionRepository {
    private val dataStore = context.applicationContext.outsideSessionDataStore

    override val currentProfile: Flow<UserProfile?> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            if (preferences[Keys.SIGNED_IN] != true) return@map null

            UserProfile(
                id = preferences[Keys.USER_ID].orEmpty(),
                email = preferences[Keys.EMAIL].orEmpty(),
                firstName = preferences[Keys.FIRST_NAME].orEmpty(),
                city = preferences[Keys.CITY].orEmpty(),
                radiusKm = preferences[Keys.RADIUS_KM] ?: DEFAULT_RADIUS_KM,
                interests = preferences[Keys.INTERESTS]
                    .orEmpty()
                    .mapNotNullTo(mutableSetOf()) { storedName ->
                        ActivityCategory.entries.firstOrNull { it.name == storedName }
                    },
                experience = preferences[Keys.EXPERIENCE]?.let { storedName ->
                    ExperienceLevel.entries.firstOrNull { it.name == storedName }
                },
                onboardingComplete = preferences[Keys.ONBOARDING_COMPLETE] == true,
            )
        }

    override suspend fun authenticate(
        email: String,
        password: String,
        mode: AuthenticationMode,
    ) {
        require(password.length >= 6) { "Use at least 6 characters." }
        val normalizedEmail = email.trim().lowercase()
        dataStore.edit { preferences ->
            val isReturningProfile = mode == AuthenticationMode.SIGN_IN &&
                preferences[Keys.EMAIL] == normalizedEmail &&
                preferences[Keys.ONBOARDING_COMPLETE] == true

            if (!isReturningProfile) {
                preferences[Keys.USER_ID] = "local-${normalizedEmail.hashCode()}"
                preferences[Keys.EMAIL] = normalizedEmail
                preferences.remove(Keys.FIRST_NAME)
                preferences.remove(Keys.CITY)
                preferences.remove(Keys.INTERESTS)
                preferences.remove(Keys.EXPERIENCE)
                preferences[Keys.RADIUS_KM] = DEFAULT_RADIUS_KM
                preferences[Keys.ONBOARDING_COMPLETE] = false
            }
            preferences[Keys.SIGNED_IN] = true
        }
    }

    override suspend fun completeOnboarding(
        firstName: String,
        city: String,
        radiusKm: Int,
        interests: Set<ActivityCategory>,
        experience: ExperienceLevel,
    ) {
        dataStore.edit { preferences ->
            check(preferences[Keys.SIGNED_IN] == true) { "A session is required to save a profile." }
            preferences[Keys.FIRST_NAME] = firstName.trim()
            preferences[Keys.CITY] = city.trim()
            preferences[Keys.RADIUS_KM] = radiusKm
            preferences[Keys.INTERESTS] = interests.mapTo(mutableSetOf()) { it.name }
            preferences[Keys.EXPERIENCE] = experience.name
            preferences[Keys.ONBOARDING_COMPLETE] = true
        }
    }

    override suspend fun signOut() {
        dataStore.edit { preferences -> preferences[Keys.SIGNED_IN] = false }
    }

    private object Keys {
        val SIGNED_IN = booleanPreferencesKey("signed_in")
        val USER_ID = stringPreferencesKey("user_id")
        val EMAIL = stringPreferencesKey("email")
        val FIRST_NAME = stringPreferencesKey("first_name")
        val CITY = stringPreferencesKey("city")
        val RADIUS_KM = intPreferencesKey("radius_km")
        val INTERESTS = stringSetPreferencesKey("interests")
        val EXPERIENCE = stringPreferencesKey("experience")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    private companion object {
        const val DEFAULT_RADIUS_KM = 20
    }
}
