package com.example.banqueapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.MainOverlay
import com.example.banqueapp.ui.screens.auth.LoginScreen
import com.example.banqueapp.ui.screens.auth.PinScreen
import com.example.banqueapp.ui.screens.auth.SignInScreen
import com.example.banqueapp.ui.screens.auth.WelcomeScreen
import com.example.banqueapp.ui.screens.settings.SettingsViewModel
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun AppNavGraph(userViewModel: UserViewModel, settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.WELCOME
    ) {

        composable(Destinations.WELCOME) {

            val isLogged = userViewModel.isLogged()

            LaunchedEffect(isLogged) {
                if (isLogged) {
                    navController.navigate(Destinations.PIN)
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
                onSignInSuccess = { navController.navigate(Destinations.WELCOME) },
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
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Destinations.HOME) {
            MainOverlay(
                userViewModel = userViewModel,
                settingsViewModel = settingsViewModel,
                onLogout = { navController.navigate(Destinations.WELCOME) {
                    popUpTo(0) { inclusive = true }
                }},
                rootNavController = navController
            )
        }

    }
}

