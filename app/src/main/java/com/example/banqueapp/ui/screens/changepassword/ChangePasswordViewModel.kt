package com.example.banqueapp.ui.screens.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.domain.repository.UserRepository
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val showCurrentPassword: Boolean = false,
    val showNewPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val passwordChangedSuccessfully: Boolean = false
)

class ChangePasswordViewModel(
    private val userRepository: UserRepository,
    private val userViewModel: UserViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            currentPassword = password,
            currentPasswordError = null
        )
    }

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            newPassword = password,
            newPasswordError = null
        )
    }

    fun updateConfirmPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = password,
            confirmPasswordError = null
        )
    }

    fun toggleCurrentPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            showCurrentPassword = !_uiState.value.showCurrentPassword
        )
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            showNewPassword = !_uiState.value.showNewPassword
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            showConfirmPassword = !_uiState.value.showConfirmPassword
        )
    }

    fun changePassword() {
        val currentState = _uiState.value
        var hasError = false

        if (currentState.currentPassword.isEmpty()) {
            _uiState.value = currentState.copy(
                currentPasswordError = "Le mot de passe actuel est requis"
            )
            hasError = true
        }

        if (currentState.newPassword.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                newPasswordError = "Le nouveau mot de passe est requis"
            )
            hasError = true
        } else if (currentState.newPassword.length < 6) {
            _uiState.value = _uiState.value.copy(
                newPasswordError = "Le mot de passe doit contenir au moins 6 caractères"
            )
            hasError = true
        } else if (currentState.newPassword.length > 16) {
            _uiState.value = _uiState.value.copy(
                newPasswordError = "Le mot de passe ne doit pas dépasser 16 caractères"
            )
            hasError = true
        } else if (!isPasswordStrong(currentState.newPassword)) {
            _uiState.value = _uiState.value.copy(
                newPasswordError = "Le mot de passe doit contenir au moins une majuscule et un caractère spécial"
            )
            hasError = true
        }

        if (currentState.confirmPassword.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                confirmPasswordError = "Veuillez confirmer le mot de passe"
            )
            hasError = true
        } else if (currentState.newPassword != currentState.confirmPassword) {
            _uiState.value = _uiState.value.copy(
                confirmPasswordError = "Les mots de passe ne correspondent pas"
            )
            hasError = true
        }

        if (hasError) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {

                val currentUser = (userViewModel.uiState.value as? UserUiState.LoggedIn)?.user

                if (currentUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = "Vous avez été déconnecté. Veuillez vous reconnecter."
                    )
                    return@launch
                }

                if (currentUser.password != currentState.currentPassword) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentPasswordError = "Le mot de passe actuel est incorrect"
                    )
                    return@launch
                }

                val updatedUser = currentUser.copy(
                    password = currentState.newPassword
                )

                userRepository.updateUser(updatedUser)

                userViewModel.updateUserPassword(updatedUser)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    passwordChangedSuccessfully = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalError = "Une erreur est survenue: ${e.message}"
                )
            }
        }
    }

    private fun isPasswordStrong(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return hasUpperCase && hasSpecialChar
    }

    fun resetSuccessState() {
        _uiState.value = _uiState.value.copy(
            passwordChangedSuccessfully = false,
            currentPassword = "",
            newPassword = "",
            confirmPassword = ""
        )
    }
}
