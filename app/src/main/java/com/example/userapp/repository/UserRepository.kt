package com.example.userapp.repository

import com.example.userapp.data.local.UserDao
import com.example.userapp.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
    suspend fun insertUser(user: UserEntity) = dao.insertUser(user)

    suspend fun deleteUser(user: UserEntity) {
        dao.deleteUser(user)
    }
}