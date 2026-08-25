package com.neverdid.outside.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neverdid.outside.data.session.AuthenticationMode
import com.neverdid.outside.data.session.SessionRepository
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ExperienceLevel
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating = _isAuthenticating.asStateFlow()

    private val _authenticationError = MutableStateFlow<String?>(null)
    val authenticationError = _authenticationError.asStateFlow()

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

    fun authenticate(
        email: String,
        password: String,
        mode: AuthenticationMode,
    ) {
        if (_isAuthenticating.value) return
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authenticationError.value = null
            runCatching { repository.authenticate(email, password, mode) }
                .onFailure { error ->
                    _authenticationError.value = error.toUserMessage()
                }
            _isAuthenticating.value = false
        }
    }

    fun clearAuthenticationError() {
        _authenticationError.value = null
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

private fun Throwable.toUserMessage(): String = when {
    message?.contains("email address is already", ignoreCase = true) == true ->
        "That email already has an account. Try signing in instead."
    message?.contains("password is invalid", ignoreCase = true) == true ||
        message?.contains("credential is incorrect", ignoreCase = true) == true ->
        "The email or password is incorrect."
    message?.contains("network", ignoreCase = true) == true ->
        "Couldn’t reach the service. Check your connection and try again."
    else -> message?.takeIf { it.isNotBlank() } ?: "Something went wrong. Please try again."
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
