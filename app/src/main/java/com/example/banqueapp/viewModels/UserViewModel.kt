package com.example.banqueapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.data.mapper.UserMapper
import com.example.banqueapp.data.repository.UserRepositoryImpl
import com.example.banqueapp.domain.models.User
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepositoryImpl) : ViewModel() {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailRegex.matches(email)
    }
    fun signUp(name: String, email: String, password: String, onComplete: () -> Unit) {
        val user = User(id = 0, name = name, email = email, password = password)
        if (!isValidEmail(email)) return
        viewModelScope.launch {
            userRepository.addUser(user)
            onComplete()
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            onResult(user?.password == password)
        }
    }
/*
    fun setPin(userId: Int, pin: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            if (user != null) {
                val updatedUser = user.copy(pin = pin)
                userRepository.updateUser(updatedUser)
                onComplete()
            }
        }
    }*/

    suspend fun getCurrentUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }

    suspend fun getCurrentUserById(id: Int): User? {
        return userRepository.getUserById(id)
    }
}
