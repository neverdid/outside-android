package com.neverdid.outside

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neverdid.outside.data.AppContainer
import com.neverdid.outside.session.SessionUiState
import com.neverdid.outside.session.SessionViewModel
import com.neverdid.outside.session.SessionViewModelFactory
import com.neverdid.outside.ui.screens.AuthScreen
import com.neverdid.outside.ui.screens.OnboardingScreen

@Composable
fun OutsideRoot(container: AppContainer) {
    val factory = remember(container.sessionRepository) {
        SessionViewModelFactory(container.sessionRepository)
    }
    val sessionViewModel: SessionViewModel = viewModel(factory = factory)
    val uiState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    val isAuthenticating by sessionViewModel.isAuthenticating.collectAsStateWithLifecycle()
    val authenticationError by sessionViewModel.authenticationError.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SessionUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        SessionUiState.SignedOut -> AuthScreen(
            backendMode = container.backendMode,
            isAuthenticating = isAuthenticating,
            authenticationError = authenticationError,
            onClearError = sessionViewModel::clearAuthenticationError,
            onAuthenticate = sessionViewModel::authenticate,
        )

        is SessionUiState.NeedsOnboarding -> OnboardingScreen(
            profile = state.profile,
            onCancel = sessionViewModel::signOut,
            onComplete = sessionViewModel::completeOnboarding,
        )

        is SessionUiState.Ready -> OutsideApp(
            profile = state.profile,
            backendMode = container.backendMode,
            repositories = container.contentRepositories,
            onSignOut = sessionViewModel::signOut,
        )
    }
}
