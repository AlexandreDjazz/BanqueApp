package com.example.banqueapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val name: String = "Alexandre Helleux",
    val email: String = "Alexandre.Helleux@hotmail.fr",
    val phone: String = "+33 6 12 34 56 78",
    val accountNumber: String = "FR76 1234 5678 9012 3456 7890 123",
    val memberSince: String = "Janvier 2023"
)

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val isLoading: Boolean = false
)

//actuellement pas fini, manque apport de données via la bdd
class ProfileViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile() {
        _uiState.value = _uiState.value.copy(
            userProfile = UserProfile(),
            isLoading = false
        )
    }

    fun updateProfile(name: String, email: String, phone: String) {
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(
                name = name,
                email = email,
                phone = phone
            )
        )
    }
}