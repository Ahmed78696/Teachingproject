package com.example.userapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.userapp.data.local.UserEntity
import com.example.userapp.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val users: StateFlow<List<UserEntity>> =
        repository.getAllUsers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addUser(first: String, last: String) {
        if (first.isNotBlank() && last.isNotBlank()) {
            viewModelScope.launch {
                repository.insertUser(UserEntity(firstName = first, lastName = last))
            }
        }
    }

        fun deleteUser(user: UserEntity) {
            viewModelScope.launch {
                repository.deleteUser(user)

            }
        }
    }



class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
