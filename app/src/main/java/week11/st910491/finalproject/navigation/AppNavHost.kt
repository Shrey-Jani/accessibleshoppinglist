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
                navController = navController
            )
        }

        // Settings (now receives navController so we can show Back)
        composable(Routes.SETTINGS) {
            SettingsScreen(
                navController = navController
            )
        }

        // Add/Edit item
        composable(Routes.ADD_EDIT_ITEM) {
            AddEditItemScreen(
                navController = navController
            )
        }
    }
}
