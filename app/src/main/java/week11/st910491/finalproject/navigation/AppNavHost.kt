package week11.st910491.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType

import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import week11.st910491.finalproject.ui.addedit.AddEditItemScreen
import week11.st910491.finalproject.ui.auth.AuthViewModel
import week11.st910491.finalproject.ui.auth.ForgotPasswordScreen
import week11.st910491.finalproject.ui.auth.LoginScreen
import week11.st910491.finalproject.ui.auth.RegisterScreen
import week11.st910491.finalproject.ui.settings.SettingsScreen
import week11.st910491.finalproject.ui.shoppinglist.ShoppingListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userPreferencesRepository: week11.st910491.finalproject.data.UserPreferencesRepository,
    isOneHanded: Boolean,
    hasSeenOnboarding: Boolean
) {
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    
    // Logic: If not seen onboarding -> Onboarding. Else if logged in -> Shopping List. Else -> Login.
    val startDest = if (!hasSeenOnboarding) Routes.ONBOARDING else if (isLoggedIn) Routes.SHOPPING_LIST else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        // Onboarding
        composable(Routes.ONBOARDING) {
            week11.st910491.finalproject.ui.onboarding.OnboardingScreen(
                onFinish = {
                    // Save preference and navigate to Login
                    kotlinx.coroutines.GlobalScope.launch { // Ideally use a ViewModel or scope passed down
                        userPreferencesRepository.setHasSeenOnboarding(true)
                    }
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // Login
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.SHOPPING_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                    authViewModel.resetLoginSuccess()
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgot = { navController.navigate(Routes.FORGOT) }
            )
        }

        // Register
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                    authViewModel.resetRegisterSuccess()
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // Forgot password
        composable(Routes.FORGOT) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onResetDone = { navController.popBackStack() }
            )
        }

        // Shopping list
        composable(Routes.SHOPPING_LIST) {
            ShoppingListScreen(
                navController = navController,
                isOneHanded = isOneHanded,
                onLogout = {
                    // UX FIX: Reset One-Handed Mode on Logout so user isn't trapped
                    kotlinx.coroutines.GlobalScope.launch {
                        userPreferencesRepository.setOneHanded(false)
                    }
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SHOPPING_LIST) { inclusive = true }
                    }
                }
            )
        }

        // Settings (now receives navController so we can show Back)
        composable(Routes.SETTINGS) {
            SettingsScreen(
                navController = navController,
                userPreferencesRepository = userPreferencesRepository
            )
        }

        // Add/Edit item
        composable(Routes.ADD_EDIT_ITEM) {
            AddEditItemScreen(
                navController = navController,
                itemId = null // TODO: Handle edit mode properly if needed
            )
        }

        // Add/Edit item - EDIT mode (with id)
        composable(
            route = Routes.ADD_EDIT_ITEM_WITH_ID,
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            AddEditItemScreen(
                navController = navController,
                itemId = itemId
            )
        }
    }
}
