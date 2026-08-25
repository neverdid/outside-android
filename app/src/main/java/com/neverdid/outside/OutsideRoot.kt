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
import com.neverdid.outside.data.session.SessionRepository
import com.neverdid.outside.session.SessionUiState
import com.neverdid.outside.session.SessionViewModel
import com.neverdid.outside.session.SessionViewModelFactory
import com.neverdid.outside.ui.screens.AuthScreen
import com.neverdid.outside.ui.screens.OnboardingScreen

@Composable
fun OutsideRoot(repository: SessionRepository) {
    val factory = remember(repository) { SessionViewModelFactory(repository) }
    val sessionViewModel: SessionViewModel = viewModel(factory = factory)
    val uiState by sessionViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SessionUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        SessionUiState.SignedOut -> AuthScreen(onAuthenticate = sessionViewModel::signIn)

        is SessionUiState.NeedsOnboarding -> OnboardingScreen(
            profile = state.profile,
            onCancel = sessionViewModel::signOut,
            onComplete = sessionViewModel::completeOnboarding,
        )

        is SessionUiState.Ready -> OutsideApp(
            profile = state.profile,
            onSignOut = sessionViewModel::signOut,
        )
    }
}
