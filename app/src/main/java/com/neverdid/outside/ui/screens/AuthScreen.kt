package com.neverdid.outside.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neverdid.outside.data.BackendMode
import com.neverdid.outside.data.session.AuthenticationMode
import com.neverdid.outside.ui.onboarding.isValidEmail
import com.neverdid.outside.ui.onboarding.isValidPassword
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lime

private enum class AuthPage { WELCOME, CREATE_ACCOUNT, SIGN_IN }

@Composable
fun AuthScreen(
    backendMode: BackendMode,
    isAuthenticating: Boolean,
    authenticationError: String?,
    onClearError: () -> Unit,
    onAuthenticate: (String, String, AuthenticationMode) -> Unit,
) {
    var page by remember { mutableStateOf(AuthPage.WELCOME) }

    BackHandler(enabled = page != AuthPage.WELCOME) { page = AuthPage.WELCOME }

    when (page) {
        AuthPage.WELCOME -> WelcomePage(
            onCreateAccount = {
                onClearError()
                page = AuthPage.CREATE_ACCOUNT
            },
            onSignIn = {
                onClearError()
                page = AuthPage.SIGN_IN
            },
        )

        AuthPage.CREATE_ACCOUNT -> EmailAuthPage(
            title = "Create your account",
            subtitle = "A few quick choices, then you can see what people nearby want to do.",
            actionLabel = "Continue",
            mode = AuthenticationMode.CREATE_ACCOUNT,
            backendMode = backendMode,
            isAuthenticating = isAuthenticating,
            authenticationError = authenticationError,
            onClearError = onClearError,
            onBack = { page = AuthPage.WELCOME },
            onAuthenticate = onAuthenticate,
        )

        AuthPage.SIGN_IN -> EmailAuthPage(
            title = "Welcome back",
            subtitle = "Sign in to return to your plans, people, and conversations.",
            actionLabel = "Sign in",
            mode = AuthenticationMode.SIGN_IN,
            backendMode = backendMode,
            isAuthenticating = isAuthenticating,
            authenticationError = authenticationError,
            onClearError = onClearError,
            onBack = { page = AuthPage.WELCOME },
            onAuthenticate = onAuthenticate,
        )
    }
}

@Composable
private fun WelcomePage(
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Forest, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Forest,
                    contentDescription = null,
                    tint = Lime,
                )
            }
            Spacer(Modifier.size(12.dp))
            Text("outside", style = MaterialTheme.typography.headlineMedium)
        }

        Column(
            modifier = Modifier.padding(vertical = 44.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "More fresh air.\nMore real people.",
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = "Find small, welcoming plans around the activities you already enjoy—or want to try.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Surface(
                color = Forest,
                shape = RoundedCornerShape(28.dp),
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    WelcomeBenefit(
                        icon = Icons.Default.Hiking,
                        title = "Find your kind of plan",
                        body = "Hikes, runs, rides, camps, and casual time outside.",
                    )
                    WelcomeBenefit(
                        icon = Icons.Default.Groups,
                        title = "Know the vibe first",
                        body = "See the pace, group size, and experience level before joining.",
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onCreateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
            ) {
                Text("Create account", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text("I already have an account")
            }
            Text(
                text = "Built for meeting around shared hobbies—not collecting followers.",
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun WelcomeBenefit(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Lime.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = Lime)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(
                body,
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun EmailAuthPage(
    title: String,
    subtitle: String,
    actionLabel: String,
    mode: AuthenticationMode,
    backendMode: BackendMode,
    isAuthenticating: Boolean,
    authenticationError: String?,
    onClearError: () -> Unit,
    onBack: () -> Unit,
    onAuthenticate: (String, String, AuthenticationMode) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showValidation by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val canSubmit = isValidEmail(email) && isValidPassword(password)
    val submit = {
        showValidation = true
        if (canSubmit) onAuthenticate(email.trim(), password, mode)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(Modifier.height(26.dp))
        Text(title, style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(10.dp))
        Text(
            subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(34.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onClearError()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            placeholder = { Text("you@example.com") },
            singleLine = true,
            isError = showValidation && !isValidEmail(email),
            supportingText = {
                if (showValidation && !isValidEmail(email)) Text("Enter a valid email address.")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            shape = RoundedCornerShape(18.dp),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                onClearError()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = showValidation && !isValidPassword(password),
            supportingText = {
                if (showValidation && !isValidPassword(password)) Text("Use at least 6 characters.")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            shape = RoundedCornerShape(18.dp),
        )
        Spacer(Modifier.height(22.dp))
        if (authenticationError != null) {
            Text(
                text = authenticationError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Button(
            onClick = submit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            enabled = !isAuthenticating,
        ) {
            if (isAuthenticating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(actionLabel, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            text = if (backendMode == BackendMode.FIREBASE) {
                "Secure account powered by Firebase Authentication. Outside never stores your raw password."
            } else {
                "Demo mode: this session stays on this device and your password is never saved. Add Firebase configuration to enable real accounts."
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back to welcome")
        }
    }
}
