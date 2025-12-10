package com.example.banqueapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.banqueapp.data.db.DatabaseProvider
import com.example.banqueapp.data.repository.UserRepositoryImpl
import com.example.banqueapp.ui.navigation.AppNavGraph
import com.example.banqueapp.ui.theme.BanqueAppTheme
import com.example.banqueapp.viewModels.UserViewModel

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(applicationContext)

        val userRepository = UserRepositoryImpl(db.userDao())

        userViewModel = UserViewModel(userRepository)
        setContent {
            BanqueAppTheme {
                AppNavGraph(userViewModel = userViewModel)
            }
        }
    }
}
