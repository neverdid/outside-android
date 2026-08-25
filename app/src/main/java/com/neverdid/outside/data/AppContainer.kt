package com.neverdid.outside.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.neverdid.outside.BuildConfig
import com.neverdid.outside.data.content.ContentRepositories
import com.neverdid.outside.data.content.demoContentRepositories
import com.neverdid.outside.data.content.firebase.firebaseContentRepositories
import com.neverdid.outside.data.session.FirebaseSessionRepository
import com.neverdid.outside.data.session.LocalSessionRepository
import com.neverdid.outside.data.session.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

data class AppContainer(
    val backendMode: BackendMode,
    val sessionRepository: SessionRepository,
    val contentRepositories: ContentRepositories,
) {
    companion object {
        fun create(context: Context): AppContainer {
            val appContext = context.applicationContext
            if (!BuildConfig.FIREBASE_CONFIGURED) {
                return AppContainer(
                    backendMode = BackendMode.DEMO,
                    sessionRepository = LocalSessionRepository(appContext),
                    contentRepositories = demoContentRepositories(),
                )
            }

            val firebaseApp = FirebaseApp.getApps(appContext).firstOrNull()
                ?: checkNotNull(FirebaseApp.initializeApp(appContext)) {
                    "Firebase configuration exists but Firebase could not initialize."
                }
            val auth = FirebaseAuth.getInstance(firebaseApp)
            val firestore = FirebaseFirestore.getInstance(firebaseApp)
            val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            return AppContainer(
                backendMode = BackendMode.FIREBASE,
                sessionRepository = FirebaseSessionRepository(auth, firestore),
                contentRepositories = firebaseContentRepositories(auth, firestore, applicationScope),
            )
        }
    }
}
