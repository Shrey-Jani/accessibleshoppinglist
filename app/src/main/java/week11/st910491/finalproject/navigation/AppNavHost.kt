package week11.st910491.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
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
    authViewModel: AuthViewModel
) {
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.SHOPPING_LIST else Routes.LOGIN
    ) {
        // Login
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.SHOPPING_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgot = {
                    navController.navigate(Routes.FORGOT)
                }
            )
        }

        // Register
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.SHOPPING_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
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

        // Shopping list (your main screen – currently no parameters)
        composable(Routes.SHOPPING_LIST) {
            ShoppingListScreen()
        }

        // Settings
        composable(Routes.SETTINGS) {
            SettingsScreen()
        }

        // Add / Edit item
        composable(Routes.ADD_EDIT_ITEM) {
            AddEditItemScreen()
        }

        composable(Routes.SHOPPING_LIST) {
            ShoppingListScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SHOPPING_LIST) { inclusive = true }
                    }
                }
            )
        }

    }
}
