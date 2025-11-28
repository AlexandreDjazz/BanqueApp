package com.example.banqueapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.data.repository.UserRepositoryImpl
import com.example.banqueapp.domain.models.User
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    var currentUser: User? = null
        private set
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailRegex.matches(email)
    }

    fun isValidPin(pin: String): Boolean {
        val pinRegex = """\d{6}""".toRegex()
        return pinRegex.matches(pin)
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        pin: String,
        onSuccess: (Boolean) -> Unit
    ) {
        val user = User(id = 0, name = name, email = email, password = password, pin = pin)
        if (!isValidEmail(email)) return
        if (!isValidPin(pin)) return
        viewModelScope.launch {
            userRepository.addUser(user)
            onSuccess(true)
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            if(user?.password == password) {
                currentUser = user
                onSuccess(true)
            } else {
                onSuccess(false)
            }
        }
    }

    fun checkPin(inputPin: String): Boolean {

        return currentUser?.pin == inputPin
    }
}

