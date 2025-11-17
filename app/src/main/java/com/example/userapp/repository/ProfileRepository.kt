package com.example.userapp.repository

import com.example.userapp.data.local.AppDatabase
import com.example.userapp.data.local.UserProfileEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class ProfileRepository(
    private val db: AppDatabase,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("users")

    private fun currentUid(): String? = auth.currentUser?.uid

    // Local profile from Room (Flow)
    fun getLocalProfile(): Flow<UserProfileEntity?> {
        val uid = currentUid() ?: return flowOf(null)
        return db.userProfileDao().getProfile(uid)
    }

    // Sync from Firestore → Room
    suspend fun refreshFromRemote() {
        val uid = currentUid() ?: return
        val snapshot = collection.document(uid).get().await()
        if (!snapshot.exists()) return

        val firstName = snapshot.getString("firstName") ?: ""
        val lastName = snapshot.getString("lastName") ?: ""
        val email = snapshot.getString("email") ?: (auth.currentUser?.email ?: "")
        val phone = snapshot.getString("phone") ?: ""
        val dobString = snapshot.getString("dateOfBirth")
        val dob = dobString?.takeIf { it.isNotBlank() }?.let(LocalDate::parse)

        val entity = UserProfileEntity(
            uid = uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            dateOfBirth = dob
        )

        db.userProfileDao().upsertProfile(entity)
    }

    // Save to Firestore + Room
    suspend fun saveProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        dateOfBirth: LocalDate?
    ) {
        val uid = currentUid() ?: throw IllegalStateException("User not logged in")

        val entity = UserProfileEntity(
            uid = uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            dateOfBirth = dateOfBirth
        )

        val data = hashMapOf(
            "uid" to uid,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone,
            "dateOfBirth" to (dateOfBirth?.toString() ?: "")
        )

        // Save remote first
        collection.document(uid).set(data).await()

        // Then cache locally
        db.userProfileDao().upsertProfile(entity)
    }
}
