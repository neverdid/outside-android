package com.neverdid.outside.data.content.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.neverdid.outside.data.content.ContentRepositories
import kotlinx.coroutines.CoroutineScope

fun firebaseContentRepositories(
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    scope: CoroutineScope,
): ContentRepositories = ContentRepositories(
    activities = FirebaseActivityRepository(auth, firestore, scope),
    feed = FirebaseFeedRepository(auth, firestore, scope),
    community = FirebaseCommunityRepository(auth, firestore),
    conversations = FirebaseConversationRepository(auth, firestore),
)
