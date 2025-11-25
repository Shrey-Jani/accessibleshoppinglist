package week11.st910491.finalproject.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgot: () -> Unit
) {
    val state by viewModel.loginState.collectAsState()

    // Password visibility state (remembered across recompositions)
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login")

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onLoginEmailChange,
                label = { Text("Email") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onLoginPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            TextButton(
                onClick = { passwordVisible = !passwordVisible }
            ) {
                Text(
                    text = if (passwordVisible) "Hide password" else "Show password"
                )
            }

            if (state.errorMessage != null) {
                Text(text = state.errorMessage!!)
            }

            Button(
                enabled = !state.isLoading,
                onClick = { viewModel.login() }
            ) {
                Text(text = "Login")
            }

            Button(onClick = onNavigateToRegister) {
                Text("Create account")
            }

            Button(onClick = onNavigateToForgot) {
                Text("Forgot password")
            }

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator()
            }
        }
    }
}
