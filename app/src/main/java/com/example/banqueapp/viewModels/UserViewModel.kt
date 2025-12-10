package com.example.banqueapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.data.datastore.DataStoreManager
import com.example.banqueapp.data.repository.UserRepositoryImpl
import com.example.banqueapp.domain.models.User
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepositoryImpl,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var currentUser: User? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            dataStoreManager.currentUserFlow.collect { cu ->
                if (cu != null) {
                    val userFromDb = userRepository.getUserById(cu.id)
                    if (userFromDb != null) {
                        currentUser = userFromDb
                        return@collect
                    }
                    currentUser = null
                }
            }
        }
    }

    fun isLogged(): Boolean{
        return currentUser != null
    }

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
        onSuccess: (Boolean, String?) -> Unit
    ) {
        val user = User(id = 0, name = name, email = email, password = password, pin = pin)
        if (!isValidEmail(email)) {
            onSuccess(false, "Invalid Email")
            return
        }

        if (!isValidPin(pin)) {
            onSuccess(false, "Invalid pin")
            return
        }
        viewModelScope.launch {
            userRepository.addUser(user)
            onSuccess(true, null)
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
                dataStoreManager.saveUser(user.id, user.name, user.email)
                onSuccess(true)
            } else {
                onSuccess(false)
            }
        }
    }

    fun checkPin(inputPin: String): Boolean {
        return currentUser?.pin == inputPin
    }

    fun logout(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            dataStoreManager.clearUser()
            currentUser = null
            onDone()
        }
    }
}