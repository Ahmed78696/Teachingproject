package com.example.userapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.userapp.data.local.UserProfileEntity
import com.example.userapp.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: LocalDate? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

class ProfileViewModel(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        observeLocalProfile()
        refreshFromRemote()
    }

    private fun observeLocalProfile() {
        viewModelScope.launch {
            repo.getLocalProfile().collectLatest { profile ->
                if (profile != null) {
                    _uiState.value = _uiState.value.copy(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email,
                        phone = profile.phone,
                        dateOfBirth = profile.dateOfBirth,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun refreshFromRemote() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repo.refreshFromRemote()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, saved = false)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value, saved = false)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, saved = false)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value, saved = false)
    }

    fun onDateOfBirthChange(date: LocalDate?) {
        _uiState.value = _uiState.value.copy(dateOfBirth = date, saved = false)
    }

    fun saveProfile() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null, saved = false)
                repo.saveProfile(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    email = state.email,
                    phone = state.phone,
                    dateOfBirth = state.dateOfBirth
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    saved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                    saved = false
                )
            }
        }
    }
}

class ProfileViewModelFactory(
    private val repo: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
