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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferencesRepository = week11.st910491.finalproject.data.UserPreferencesRepository(this)

        setContent {
            val isHighContrast by userPreferencesRepository.isHighContrast.collectAsState(initial = false)
            val isLargeText by userPreferencesRepository.isLargeText.collectAsState(initial = false)
            val isOneHanded by userPreferencesRepository.isOneHanded.collectAsState(initial = false)
            val hasSeenOnboarding by userPreferencesRepository.hasSeenOnboarding.collectAsState(initial = false)

            AccessibleShoppingListTheme(
                highContrast = isHighContrast,
                largeText = isLargeText
            ) {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(
                        navController = navController,
                        authViewModel = authViewModel,
                        userPreferencesRepository = userPreferencesRepository,
                        isOneHanded = isOneHanded,
                        hasSeenOnboarding = hasSeenOnboarding
                    )
                }
            }
        }
    }
}
