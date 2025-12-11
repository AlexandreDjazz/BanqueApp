package com.example.banqueapp.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.BottomNavOverlay
import com.example.banqueapp.ui.screens.auth.LoginScreen
import com.example.banqueapp.ui.screens.auth.PinScreen
import com.example.banqueapp.ui.screens.auth.SignInScreen
import com.example.banqueapp.ui.screens.auth.WelcomeScreen
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun AppNavGraph(
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    transactionViewModel: TransactionViewModel
) {
    val navController = rememberNavController()

    Surface() {
        NavHost(
            navController = navController,
            startDestination = Destinations.WELCOME
        ) {

            composable(Destinations.WELCOME) {
                val uiState by userViewModel.uiState.collectAsState()

                LaunchedEffect(uiState) {
                    if (uiState is UserUiState.LoggedIn) {
                        navController.navigate(Destinations.PIN) {
                            popUpTo(Destinations.WELCOME) { inclusive = true }
                        }
                    }
                }

                WelcomeScreen(
                    onLoginClick = { navController.navigate(Destinations.LOGIN) },
                    onSignInClick = { navController.navigate(Destinations.SIGNIN) }
                )
            }


            composable(Destinations.LOGIN) {
                LoginScreen(
                    userViewModel = userViewModel,
                    onLoginSuccess = { navController.navigate(Destinations.PIN) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Destinations.SIGNIN) {
                SignInScreen(
                    userViewModel = userViewModel,
                    onSignInSuccess = { navController.navigate(Destinations.LOGIN) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Destinations.PIN) {
                PinScreen(
                    userViewModel = userViewModel,
                    onPinSuccess = {
                        navController.navigate(Destinations.HOME) {
                            popUpTo(Destinations.WELCOME) { inclusive = true }
                        }
                    },
                    onBack = {
                        userViewModel.onLogout()
                        navController.navigate(Destinations.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Destinations.HOME) {
                BottomNavOverlay(
                    userViewModel = userViewModel,
                    settingsViewModel = settingsViewModel,
                    transactionViewModel = transactionViewModel,
                    onLogout = { navController.navigate(Destinations.WELCOME) {
                        userViewModel.onLogout()
                        popUpTo(0) { inclusive = true }
                    }},
                )
            }
        }
    }
}

