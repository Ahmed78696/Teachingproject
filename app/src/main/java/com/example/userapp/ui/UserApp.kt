package com.example.userapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.userapp.ui.components.DrawerContent
import kotlinx.coroutines.launch
import com.example.userapp.viewmodel.UserViewModel
import com.example.userapp.viewmodel.SettingsViewModel
import com.example.userapp.ui.home.HomeScreen
import com.example.userapp.ui.users.UsersScreen
import com.example.userapp.ui.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserApp(userViewModel: UserViewModel, settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDark by settingsViewModel.isDarkTheme.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface, // solid background
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                DrawerContent(
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope,
                    isDarkTheme = isDark,
                    onThemeToggle = { settingsViewModel.toggleTheme() }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("User App") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen() }

                composable("users") {
                    UsersScreen(
                        viewModel = userViewModel,
                        onNavigateSettings = { navController.navigate("settings") } // ✅ connect nav
                    )
                }

                composable("settings") { SettingsScreen(navController, settingsViewModel) }
            }
        }
    }
}
