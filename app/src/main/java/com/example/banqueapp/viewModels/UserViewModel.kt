package com.example.banqueapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.data.datastore.DataStoreManager
import com.example.banqueapp.data.repository.UserRepositoryImpl
import com.example.banqueapp.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed class UserUiState {
    object Loading : UserUiState()
    object LoggedOut : UserUiState()
    data class LoggedIn(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

class UserViewModel(
    private val userRepository: UserRepositoryImpl,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {

        dataStoreManager.currentUserFlow
            .onEach { user ->
                if (user != null) {
                    val userFromDb = userRepository.getUserById(user.id)
                    _uiState.value = if (userFromDb != null) {
                        UserUiState.LoggedIn(userFromDb)
                    } else {
                        UserUiState.LoggedOut
                    }
                } else {
                    _uiState.value = UserUiState.LoggedOut
                }
            }
            .launchIn(viewModelScope)
    }


    fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailRegex.matches(email)
    }

    fun isValidPin(pin: String): Boolean {
        val pinRegex = """\d{6}""".toRegex()
        return pinRegex.matches(pin)
    }

    fun onSignUp(
        name: String,
        email: String,
        password: String,
        pin: String,
        onResult: ((Boolean, String?) -> Unit)
    ) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading

            if (!isValidEmail(email)) {
                _uiState.value = UserUiState.Error("Email invalide")
                onResult.invoke(false, "Email invalide")
                return@launch
            }
            if (!isValidPin(pin)) {
                _uiState.value = UserUiState.Error("PIN invalide (6 chiffres)")
                onResult.invoke(false, "PIN invalide (6 chiffres)")
                return@launch
            }

            try {
                val user = User(id = 0, name = name, email = email, password = password, pin = pin)
                userRepository.addUser(user)
                _uiState.value = UserUiState.LoggedOut
                onResult.invoke(true, null)
            } catch (e: Exception) {
                _uiState.value = UserUiState.Error("Erreur inscription")
                onResult.invoke(false, "Erreur inscription")
            }
        }
    }

    fun onLogin(email: String, password: String, onSuccess:(() -> Unit)) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading

            try {
                val user = userRepository.getUserByEmail(email)
                if (user?.password == password) {
                    dataStoreManager.saveUser(user.id)
                    _uiState.value = UserUiState.LoggedIn(user)
                    onSuccess.invoke()
                } else {
                    _uiState.value = UserUiState.Error("Email ou mot de passe incorrect")
                }
            } catch (e: Exception) {
                _uiState.value = UserUiState.Error("Erreur connexion")
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            dataStoreManager.clearUser()
            _uiState.value = UserUiState.LoggedOut
        }
    }

    fun checkPin(inputPin: String): Boolean {
        return (uiState.value as? UserUiState.LoggedIn)?.user?.pin == inputPin
    }

    fun isLogged() = uiState.value is UserUiState.LoggedIn
}