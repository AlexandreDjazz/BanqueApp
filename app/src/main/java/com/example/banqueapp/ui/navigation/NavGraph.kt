package com.example.banqueapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.*
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun AppNavGraph(
    userViewModel: UserViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.WELCOME
    ) {

        var currentUserId: Int? = null

        composable(Destinations.WELCOME) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Destinations.LOGIN) },
                onSignInClick = { navController.navigate(Destinations.SIGNIN) }
            )
        }

        composable(Destinations.LOGIN) {
            LoginScreen(
                navController = navController,
                onLogin = { email, password ->
                    userViewModel.login(email, password) { success ->
                        if (success) {
                            navController.navigate(Destinations.PIN)
                        }
                    }
                }
            )
        }

        composable(Destinations.SIGNIN) {
            SignInScreen(
                navController = navController,
                onSignIn = { name, email, password ->
                    userViewModel.signUp(name, email, password) {
                        navController.navigate(Destinations.WELCOME)
                    }
                }
            )
        }

        composable(Destinations.PIN) {
            PinScreen(
                navController = navController,
                onPinSet = { pin ->
                    currentUserId?.let { id ->
                        userViewModel.setPin(id, pin) {
                            navController.navigate(Destinations.HOME) {
                                popUpTo(Destinations.WELCOME) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Destinations.HOME) {
            HomeScreen({})
        }
    }
}
