package com.example.banqueapp.data.repository

import com.example.banqueapp.data.db.dao.UserDao
import com.example.banqueapp.data.mapper.UserMapper
import com.example.banqueapp.domain.models.User
import com.example.banqueapp.domain.repository.UserRepository

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {

    override suspend fun addUser(user: User): Long {
        return userDao.insertUser(UserMapper.toEntity(user))
    }

    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)?.let { UserMapper.toDomain(it) }
    }

    override suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)?.let { UserMapper.toDomain(it) }
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(UserMapper.toEntity(user))
    }


    override suspend fun deleteUserById(id: Int) {
        userDao.deleteUserById(id)
    }

    override suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }
}
