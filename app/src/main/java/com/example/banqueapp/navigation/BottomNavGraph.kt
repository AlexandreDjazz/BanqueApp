package com.example.banqueapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.banqueapp.ui.screens.HomeScreen
import com.example.banqueapp.ui.screens.menu.DebugMenuScreen
import com.example.banqueapp.ui.screens.debug.TransactionDebugScreen
import com.example.banqueapp.ui.screens.debug.VirementDebugScreen
import com.example.banqueapp.ui.screens.graph.GraphMenuScreen
import com.example.banqueapp.ui.screens.marches.MarchesScreen
import com.example.banqueapp.ui.screens.menu.MapScreen
import com.example.banqueapp.ui.screens.menu.SubMenuScreen
import com.example.banqueapp.ui.screens.profile.EditProfileScreen
import com.example.banqueapp.ui.screens.profile.ProfileScreen
import com.example.banqueapp.ui.screens.profile.SettingsScreen
import com.example.banqueapp.ui.screens.profile.SupportScreen
import com.example.banqueapp.ui.screens.transaction.AllTransactionsScreen
import com.example.banqueapp.ui.screens.virements.VirementsScreen
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.VirementViewModel
import com.google.maps.android.compose.rememberComposeUiViewRenderer

@Composable
fun BottomNavGraph(
    bottomNavController: NavHostController,
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    transactionViewModel: TransactionViewModel,
    virementViewModel: VirementViewModel,
    innerPadding: PaddingValues,
    onLogout: () -> Unit
) {

    val uiState by userViewModel.uiState.collectAsState()
    val currentUser = (uiState as? UserUiState.LoggedIn)?.user

    NavHost(
        navController = bottomNavController,
        startDestination = Destinations.HOME,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Destinations.HOME) {
            HomeScreen(
                userViewModel = userViewModel,
                transactionViewModel = transactionViewModel,
                onSeeAllTransaction = {bottomNavController.navigate(Destinations.ALL_TRANSACTIONS)},
                openGraph = {bottomNavController.navigate(Destinations.GRAPH)}
            )
        }

        composable(Destinations.PROFILE) {
            ProfileScreen(
                onNavigateToSettings = { bottomNavController.navigate(Destinations.SETTINGS) },
                onNavigateToEditProfile = { bottomNavController.navigate(Destinations.EDIT_PROFILE) },
                onNavigateToSupport = { bottomNavController.navigate(Destinations.SUPPORT) },
                onLogout = onLogout,
                userViewModel = userViewModel
            )
        }

        composable(Destinations.EDIT_PROFILE) {
            EditProfileScreen(
                user = currentUser,
                userViewModel = userViewModel,
                onCancel = { bottomNavController.popBackStack() }
            )
        }


        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { bottomNavController.popBackStack() },
                onNavigateToChangePassword = {},
                viewModel = settingsViewModel
            )
        }

        composable(Destinations.SUPPORT) {
            SupportScreen(
                onBack = {bottomNavController.popBackStack()}
            )
        }

        composable(Destinations.SUB_MENU) {
            SubMenuScreen(
                navController = bottomNavController,
            )
        }

        composable(Destinations.ALL_TRANSACTIONS) {
            AllTransactionsScreen(
                transactionViewModel = transactionViewModel,
                userViewModel = userViewModel,
                onBack = {bottomNavController.navigateUp()}
            )
        }

        composable(Destinations.GRAPH) {
            GraphMenuScreen(
                onNavigateBack = { bottomNavController.navigateUp() },
                transactionViewModel = transactionViewModel,
                userViewModel = userViewModel
            )
        }

        composable(SubMenuDestinations.MAP) {
            MapScreen(
                onNavigateBack = { bottomNavController.navigateUp() },
                showBackButton = true
            )
        }

        composable(SubMenuDestinations.MARCHES) {
            MarchesScreen(
                onNavigateBack = {bottomNavController.popBackStack()}
            )
        }

        composable(SubMenuDestinations.VIREMENT) {
            VirementsScreen(
                virementViewModel = virementViewModel,
                userViewModel = userViewModel,
                onBack = { bottomNavController.popBackStack() }
            )
        }






        // ------------------------- // DEBUG SYSTEM

        composable(SubMenuDestinations.DEBUG) {
            DebugMenuScreen(
                navController = bottomNavController,
                onBack = { bottomNavController.navigateUp() }
            )
        }

        composable(DebugMenuDestinations.TRANSACTION) {
            TransactionDebugScreen(
                transactionViewModel = transactionViewModel,
                userViewModel = userViewModel,
                onBack = { bottomNavController.navigateUp() }
            )
        }

        composable(DebugMenuDestinations.VIREMENT) {
            VirementDebugScreen(
                virementViewModel = virementViewModel,
                userViewModel = userViewModel,
                onBack = { bottomNavController.navigateUp() }
            )
        }
    }
}
