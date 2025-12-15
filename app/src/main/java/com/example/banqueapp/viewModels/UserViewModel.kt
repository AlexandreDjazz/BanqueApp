package com.example.banqueapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.data.datastore.DataStoreManager
import com.example.banqueapp.domain.models.User
import com.example.banqueapp.domain.repository.UserRepository
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
    object SignUpSuccess : UserUiState()
    data class Error(val message: String) : UserUiState()
}

class UserViewModel(
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        loadUser()
        loadUsers()
    }

    fun loadUser() {
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

    private fun loadUsers() {
        viewModelScope.launch {
            _users.value = getDebugUsers()
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailRegex.matches(email)
    }

    fun isValidPin(pin: String): Boolean {
        val pinRegex = """\d{6}""".toRegex()
        return pinRegex.matches(pin)
    }

    fun isValidPassword(password: String): Boolean {
        val passwordRegex = """^(?=.*[A-Z])(?=.*[@#\$%^&+=!]).{8,16}$""".toRegex()
        return passwordRegex.matches(password)
    }

    fun isValidName(name: String): Boolean {
        val nameRegex = """^[A-Za-z]{2,20}$""".toRegex()
        return nameRegex.matches(name)
    }

    fun isValidPhone(name: String): Boolean {
        val phoneRegex = """\d{10}""".toRegex()
        return phoneRegex.matches(name)
    }

    fun onSignUp(
        name: String,
        email: String,
        phone: String,
        password: String,
        pin: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading

            if (!isValidName(name)) {
                _uiState.value = UserUiState.Error(
                    "Nom invalide : 2 à 20 lettres, sans espaces ni chiffres"
                )
                onResult(false, "Nom invalide : 2 à 20 lettres, sans espaces ni chiffres")
                return@launch
            }

            if (!isValidEmail(email)) {
                _uiState.value = UserUiState.Error("Email invalide")
                onResult(false, "Email invalide")
                return@launch
            }

            val existingUser = userRepository.getUserByEmail(email)
            if (existingUser != null) {
                _uiState.value = UserUiState.Error("Email déjà utilisé")
                onResult(false, "Email déjà utilisé")
                return@launch
            }

            if (!isValidPassword(password)) {
                _uiState.value = UserUiState.Error("Mot de passe invalide : 6-16 caractères, 1 majuscule et 1 caractère spécial")
                onResult(false, "Mot de passe invalide : 6-16 caractères, 1 majuscule et 1 caractère spécial")
                return@launch
            }


            if (!isValidPin(pin)) {
                _uiState.value = UserUiState.Error("PIN invalide (6 chiffres)")
                onResult(false, "PIN invalide (6 chiffres)")
                return@launch
            }

            if (!isValidPhone(phone)) {
                _uiState.value = UserUiState.Error("Numéro de téléphone invalide (10 chiffres)")
                onResult(false, "Numéro de téléphone invalide (10 chiffres)")
                return@launch
            }


            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || pin.isBlank()) {
                _uiState.value = UserUiState.Error("Tous les champs doivent être remplis")
                onResult(false, "Tous les champs doivent être remplis")
                return@launch
            }

            val user = User(
                id = 0,
                name = name,
                email = email,
                phone = phone,
                password = password,
                pin = pin
            )

            userRepository.addUser(user)
            _uiState.value = UserUiState.SignUpSuccess
            onResult(true, null)
        }
    }

    fun onLogin(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading

            val user = userRepository.getUserByEmail(email)
            if (user == null || user.password != password) {
                _uiState.value = UserUiState.Error("Email ou mot de passe incorrect")
                return@launch
            }

            dataStoreManager.saveUser(user.id)
            _uiState.value = UserUiState.LoggedIn(user)
            onSuccess()
        }
    }

    fun onLogout(onComplete: () -> Unit) {
        viewModelScope.launch {
            dataStoreManager.clearUser()
            _uiState.value = UserUiState.LoggedOut
            onComplete()
        }
    }

    suspend fun updateProfile(
        name: String,
        email: String,
        phone: String
    ): Boolean {
        val currentUser =
            (uiState.value as? UserUiState.LoggedIn)?.user ?: return false

        val updatedUser = currentUser.copy(
            name = name,
            email = email,
            phone = phone
        )

        userRepository.updateUser(updatedUser)
        _uiState.value = UserUiState.LoggedIn(updatedUser)
        return true
    }

    fun updateBalance(userID: Int, amount: Double){
        viewModelScope.launch {
            userRepository.updateBalance(userID, amount)
            val currentUser = (uiState.value as? UserUiState.LoggedIn)?.user
            if (currentUser != null && currentUser.id == userID) {
                val updatedUser = currentUser.copy(balance = currentUser.balance + amount)
                _uiState.value = UserUiState.LoggedIn(updatedUser)
            }
        }
    }


    fun checkPin(inputPin: String): Boolean {
        return (uiState.value as? UserUiState.LoggedIn)?.user?.pin == inputPin
    }

    fun isLogged() = uiState.value is UserUiState.LoggedIn

    suspend fun getDebugUsers(): List<User>{
        return userRepository.getUsers()
    }
}
