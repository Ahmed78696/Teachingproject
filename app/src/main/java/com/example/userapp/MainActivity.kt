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
import com.example.userapp.repository.ProfileRepository
import com.example.userapp.repository.UserRepository
import com.example.userapp.ui.UserApp
import com.example.userapp.ui.login.LoginScreen
import com.example.userapp.ui.theme.UserAppTheme
import com.example.userapp.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// DataStore extension
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- DataStore for settings ---
        val userPrefsRepo = UserPreferencesRepository(applicationContext.dataStore)
        val settingsFactory = SettingsViewModelFactory(userPrefsRepo)
        val settingsViewModel: SettingsViewModel by viewModels { settingsFactory }

        // --- Room database ---
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val userRepository = UserRepository(db.userDao())

        // --- Profile repository (Room + Firestore) ---
        val profileRepository = ProfileRepository(
            db = db,
            firestore = FirebaseFirestore.getInstance(),
            auth = FirebaseAuth.getInstance()
        )

        val userFactory = UserViewModelFactory(userRepository)
        val userViewModel: UserViewModel by viewModels { userFactory }

        val profileViewModelFactory = ProfileViewModelFactory(profileRepository)
        val profileViewModel: ProfileViewModel by viewModels { profileViewModelFactory }

        setContent {
            val isDark by settingsViewModel.isDarkTheme.collectAsState()

            UserAppTheme(darkTheme = isDark) {
                if (loginViewModel.isLoggedIn()) {
                    UserApp(
                        userViewModel = userViewModel,
                        settingsViewModel = settingsViewModel,
                        profileViewModel = profileViewModel
                    )
                } else {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { recreate() }
                    )
                }
            }
        }
    }
}
