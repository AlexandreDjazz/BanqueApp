package com.example.banqueapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.HomeScreen
import com.example.banqueapp.ui.screens.LoginScreen
import com.example.banqueapp.ui.screens.PinScreen
import com.example.banqueapp.ui.screens.SignInScreen
import com.example.banqueapp.ui.screens.WelcomeScreen
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun AppNavGraph(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.WELCOME
    ) {

        composable(Destinations.WELCOME) {
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
                onPinSuccess = { navController.navigate(Destinations.HOME) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.HOME) {
            HomeScreen(
                onLogout = { navController.navigate(Destinations.WELCOME) }
            )
        }
    }
}

