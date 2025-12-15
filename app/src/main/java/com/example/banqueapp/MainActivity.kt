package com.example.banqueapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.banqueapp.data.datastore.DataStoreManager
import com.example.banqueapp.data.db.DatabaseProvider
import com.example.banqueapp.data.repository.UserRepositoryImpl
import com.example.banqueapp.navigation.AppNavGraph
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.banqueapp.data.repository.TransactionRepositoryImpl
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.ThemeMode
import com.example.banqueapp.ui.theme.BanqueAppTheme
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.VirementViewModel
import com.example.banqueapp.ui.screens.changepassword.ChangePasswordViewModel

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var virementViewModel: VirementViewModel
    private lateinit var changePasswordViewModel: ChangePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(applicationContext)

        val userRepository = UserRepositoryImpl(db.userDao())
        val transactionRepository = TransactionRepositoryImpl(db.transactionDao(), db.userDao())
        val dataStoreManager = DataStoreManager(applicationContext)



        userViewModel = UserViewModel(userRepository, dataStoreManager)
        transactionViewModel = TransactionViewModel(transactionRepository)
        virementViewModel = VirementViewModel(transactionRepository, userRepository)
        changePasswordViewModel = ChangePasswordViewModel(userRepository, userViewModel)

        setContent {
            BanqueApp(
                userViewModel = userViewModel,
                transactionViewModel = transactionViewModel,
                virementViewModel = virementViewModel,
                changePasswordViewModel = changePasswordViewModel
            )
        }
    }
}

@Composable
fun BanqueApp(
    userViewModel: UserViewModel,
    transactionViewModel: TransactionViewModel,
    virementViewModel: VirementViewModel,
    changePasswordViewModel: ChangePasswordViewModel
) {
    val settingsViewModel: SettingsViewModel = viewModel()

    val uiState by settingsViewModel.uiState.collectAsState()
    val systemInDarkTheme = isSystemInDarkTheme()

    val darkTheme = when (uiState.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
    }

    BanqueAppTheme(darkTheme = darkTheme) {

        AppNavGraph(
            userViewModel = userViewModel,
            settingsViewModel = settingsViewModel,
            transactionViewModel = transactionViewModel,
            virementViewModel = virementViewModel,
            changePasswordViewModel = changePasswordViewModel
        )
    }
}