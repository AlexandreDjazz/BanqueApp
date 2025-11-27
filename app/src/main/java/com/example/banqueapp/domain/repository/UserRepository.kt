package com.example.banqueapp.domain.repository

import com.example.banqueapp.domain.models.User

interface UserRepository {
    suspend fun addUser(user: User): Long
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserById(id: Int): User?
    suspend fun updateUser(user: User)
    suspend fun deleteUserById(id: Int)
    suspend fun deleteAllUsers()
}

