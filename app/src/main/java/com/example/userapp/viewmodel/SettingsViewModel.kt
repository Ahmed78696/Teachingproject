package com.example.userapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userapp.data.prefs.UserPreferencesRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repo: UserPreferencesRepository
) : ViewModel() {

    // Observe dark theme preference
    val isDarkTheme: StateFlow<Boolean> = repo.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggleTheme() {
        viewModelScope.launch {
            repo.toggleTheme()
        }
    }
}

