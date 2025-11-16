package com.example.userapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Navigation", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        Text(
            "Home",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch { drawerState.close() }
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
        )

        Text(
            "Users",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch { drawerState.close() }
                    navController.navigate("users")
                }
        )

        Text(
            "Profile",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch { drawerState.close() }
                    navController.navigate("profile")
                }
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (isDarkTheme) "Dark Mode" else "Light Mode")
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onThemeToggle() }
            )
        }
    }
}
