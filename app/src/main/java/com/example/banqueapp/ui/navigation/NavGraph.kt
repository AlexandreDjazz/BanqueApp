package com.example.banqueapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.LoginScreen
import com.example.banqueapp.ui.screens.PinScreen
import com.example.banqueapp.ui.screens.SignInScreen
import com.example.banqueapp.ui.screens.WelcomeScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
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
            LoginScreen({ email, password ->
                navController.navigate(Destinations.PIN)
                // ici tu peux appeler ton ViewModel pour la connexion
            }, navController)
        }

        composable(Destinations.SIGNIN) {
            SignInScreen({ name, email, password ->
                // ici tu peux appeler ton ViewModel pour l'inscription
            }, navController)
        }

        composable(Destinations.PIN) {
            PinScreen(navController = navController) { pin ->
                // ici tu peux vérifier le PIN et naviguer vers l'écran principal de l'app
            }
        }
    }
}
