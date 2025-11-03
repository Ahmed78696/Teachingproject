package com.example.userapp.ui.users


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.userapp.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.userapp.data.local.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(viewModel: UserViewModel,
                onNavigateSettings: () -> Unit = {} //  allows MainActivity/NavHost to handle navigation
) {
    val users by viewModel.users.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") },
                actions = {
                    IconButton(onClick = onNavigateSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // --- Add User Section ---
            Text("Add User", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            TextField(
                value = first,
                onValueChange = { first = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = last,
                onValueChange = { last = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (first.isNotBlank() && last.isNotBlank()) {
                        viewModel.addUser(first, last)
                        first = ""
                        last = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("User added successfully!")
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add User")
            }

            Spacer(Modifier.height(16.dp))
            Text("User List:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // --- User List with Delete Button ---
            LazyColumn {
                items(users, key = { it.id }) { user ->
                    UserRow(
                        user = user,
                        onDelete = {
                            viewModel.deleteUser(user)
                            scope.launch {
                                snackbarHostState.showSnackbar("Deleted ${user.firstName}")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserRow(
    user: UserEntity,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${user.firstName} ${user.lastName}")
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete user",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}