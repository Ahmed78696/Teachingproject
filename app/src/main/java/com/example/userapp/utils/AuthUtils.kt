package com.example.userapp.utils

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun loginUser(
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onError(task.exception?.message ?: "Login failed")
            }
        }
}
