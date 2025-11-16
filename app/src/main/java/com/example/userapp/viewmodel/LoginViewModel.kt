package com.example.userapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.userapp.utils.loginUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email and password cannot be empty")
            return
        }

        _loginState.value = LoginState.Loading

        loginUser(
            email = email,
            password = password,
            onSuccess = {
                _loginState.value = LoginState.Success
            },
            onError = { msg ->
                _loginState.value = LoginState.Error(msg)
            }
        )
    }

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email and password cannot be empty")
            return
        }

        _loginState.value = LoginState.Loading

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value =
                        LoginState.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun isLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }
}
