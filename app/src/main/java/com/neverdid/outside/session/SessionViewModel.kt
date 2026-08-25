package com.neverdid.outside.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neverdid.outside.data.session.SessionRepository
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ExperienceLevel
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SessionUiState {
    data object Loading : SessionUiState
    data object SignedOut : SessionUiState
    data class NeedsOnboarding(val profile: UserProfile) : SessionUiState
    data class Ready(val profile: UserProfile) : SessionUiState
}

class SessionViewModel(
    private val repository: SessionRepository,
) : ViewModel() {
    val uiState: StateFlow<SessionUiState> = repository.currentProfile
        .map { profile ->
            when {
                profile == null -> SessionUiState.SignedOut
                profile.onboardingComplete -> SessionUiState.Ready(profile)
                else -> SessionUiState.NeedsOnboarding(profile)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SessionUiState.Loading,
        )

    fun signIn(email: String) {
        viewModelScope.launch { repository.signIn(email) }
    }

    fun completeOnboarding(
        firstName: String,
        city: String,
        radiusKm: Int,
        interests: Set<ActivityCategory>,
        experience: ExperienceLevel,
    ) {
        viewModelScope.launch {
            repository.completeOnboarding(firstName, city, radiusKm, interests, experience)
        }
    }

    fun signOut() {
        viewModelScope.launch { repository.signOut() }
    }
}

class SessionViewModelFactory(
    private val repository: SessionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SessionViewModel::class.java))
        return SessionViewModel(repository) as T
    }
}
