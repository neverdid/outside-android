package com.neverdid.outside.data.session

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ExperienceLevel
import com.neverdid.outside.model.UserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class FirebaseSessionRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : SessionRepository {
    private val authenticatedUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentProfile: Flow<UserProfile?> = authenticatedUser.flatMapLatest { user ->
        if (user == null) flowOf(null) else observeProfile(user)
    }

    override suspend fun authenticate(
        email: String,
        password: String,
        mode: AuthenticationMode,
    ) {
        val normalizedEmail = email.trim().lowercase()
        val result = when (mode) {
            AuthenticationMode.CREATE_ACCOUNT ->
                auth.createUserWithEmailAndPassword(normalizedEmail, password).await()
            AuthenticationMode.SIGN_IN ->
                auth.signInWithEmailAndPassword(normalizedEmail, password).await()
        }
        val user = checkNotNull(result.user) { "Firebase did not return an authenticated user." }
        firestore.collection(USERS).document(user.uid).set(
            mapOf(
                "email" to normalizedEmail,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        ).await()
    }

    override suspend fun completeOnboarding(
        firstName: String,
        city: String,
        radiusKm: Int,
        interests: Set<ActivityCategory>,
        experience: ExperienceLevel,
    ) {
        val user = checkNotNull(auth.currentUser) { "A session is required to save a profile." }
        firestore.collection(USERS).document(user.uid).set(
            mapOf(
                "email" to user.email.orEmpty(),
                "firstName" to firstName.trim(),
                "city" to city.trim(),
                "radiusKm" to radiusKm,
                "interests" to interests.map { it.name },
                "experience" to experience.name,
                "onboardingComplete" to true,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        ).await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    private fun observeProfile(user: FirebaseUser): Flow<UserProfile> = callbackFlow {
        val registration = firestore.collection(USERS).document(user.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val profile = UserProfile(
                    id = user.uid,
                    email = snapshot?.getString("email") ?: user.email.orEmpty(),
                    firstName = snapshot?.getString("firstName").orEmpty(),
                    city = snapshot?.getString("city").orEmpty(),
                    radiusKm = snapshot?.getLong("radiusKm")?.toInt() ?: 20,
                    interests = snapshot?.get("interests")
                        .asStringList()
                        .mapNotNullTo(mutableSetOf()) { storedName ->
                            ActivityCategory.entries.firstOrNull { it.name == storedName }
                        },
                    experience = snapshot?.getString("experience")?.let { storedName ->
                        ExperienceLevel.entries.firstOrNull { it.name == storedName }
                    },
                    onboardingComplete = snapshot?.getBoolean("onboardingComplete") == true,
                )
                trySend(profile)
            }
        awaitClose { registration.remove() }
    }

    private companion object {
        const val USERS = "users"
    }
}

private fun Any?.asStringList(): List<String> =
    (this as? List<*>)?.mapNotNull { it as? String }.orEmpty()
