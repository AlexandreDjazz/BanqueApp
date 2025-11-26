package com.example.banqueapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.banqueapp.ui.navigation.AppNavGraph
import com.example.banqueapp.ui.theme.BanqueAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BanqueAppTheme {
                AppNavGraph()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}