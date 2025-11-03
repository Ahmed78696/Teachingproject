package com.example.userapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.userapp.data.local.AppDatabase
import com.example.userapp.data.prefs.UserPreferencesRepository
import com.example.userapp.repository.UserRepository
import com.example.userapp.ui.UserApp
import com.example.userapp.ui.theme.UserAppTheme
import com.example.userapp.viewmodel.SettingsViewModel
import com.example.userapp.viewmodel.SettingsViewModelFactory
import com.example.userapp.viewmodel.UserViewModel
import com.example.userapp.viewmodel.UserViewModelFactory
import kotlin.jvm.java

// ✅ Global DataStore instance for the entire app
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ --- DataStore setup ---
        val userPrefsRepo = UserPreferencesRepository(dataStore)
        val settingsFactory = SettingsViewModelFactory(userPrefsRepo)
        val settingsViewModel: SettingsViewModel by viewModels { settingsFactory }

        // ✅ --- Room database + repository setup ---
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_db"
        ).build()

        val userRepository = UserRepository(db.userDao())
        val userFactory = UserViewModelFactory(userRepository)
        val userViewModel: UserViewModel by viewModels { userFactory }

        // ✅ --- Compose UI entry point ---
        setContent {
            // React to dark/light theme preference
            val isDark by settingsViewModel.isDarkTheme.collectAsState()

            UserAppTheme(darkTheme = isDark) {
                UserApp(
                    userViewModel = userViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
