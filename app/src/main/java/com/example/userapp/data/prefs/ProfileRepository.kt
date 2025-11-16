package com.example.userapp.repository

import com.example.userapp.data.local.AppDatabase
import com.example.userapp.data.local.UserProfileEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class ProfileRepository(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val profileCollection = firestore.collection("users")

    private fun currentUid(): String? = auth.currentUser?.uid

    fun getLocalProfile(): Flow<UserProfileEntity?> {
        val uid = currentUid() ?: ""
        return db.userProfileDao().getProfile(uid)
    }

    suspend fun syncFromRemote() {
        val uid = currentUid() ?: return
        val doc = profileCollection.document(uid).get().await()
        if (doc.exists()) {
            val firstName = doc.getString("firstName") ?: ""
            val lastName = doc.getString("lastName") ?: ""
            val email = doc.getString("email") ?: (auth.currentUser?.email ?: "")
            val phone = doc.getString("phone") ?: ""
            val dobString = doc.getString("dateOfBirth")
            val dob = dobString?.let { LocalDate.parse(it) }

            val profile = UserProfileEntity(
                uid = uid,
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                dateOfBirth = dob
            )
            db.userProfileDao().upsertProfile(profile)
        }
    }

    suspend fun saveProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        dateOfBirth: LocalDate?
    ) {
        val uid = currentUid() ?: return

        val profile = UserProfileEntity(
            uid = uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            dateOfBirth = dateOfBirth
        )

        // Save to Firestore
        val data = hashMapOf(
            "uid" to uid,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone,
            "dateOfBirth" to (dateOfBirth?.toString() ?: "")
        )

        profileCollection.document(uid).set(data).await()

        // Save to Room
        db.userProfileDao().upsertProfile(profile)
    }
}
