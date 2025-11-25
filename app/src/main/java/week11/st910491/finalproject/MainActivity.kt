package week11.st910491.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import week11.st910491.finalproject.navigation.AppNavHost
import week11.st910491.finalproject.ui.auth.AuthViewModel
import week11.st910491.finalproject.ui.theme.AccessibleShoppingListTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccessibleShoppingListTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
