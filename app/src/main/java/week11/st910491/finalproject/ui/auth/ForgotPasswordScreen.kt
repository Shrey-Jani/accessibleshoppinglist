package week11.st910491.finalproject.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onResetDone: () -> Unit
) {
    val state by viewModel.forgotState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onResetDone()
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
            Text(text = "Reset Password")

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onForgotEmailChange,
                label = { Text("Email") },
                singleLine = true
            )

            if (state.errorMessage != null) {
                Text(text = state.errorMessage!!)
            }

            Button(
                enabled = !state.isLoading,
                onClick = { viewModel.sendResetEmail() }
            ) {
                Text("Send reset email")
            }

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator()
            }
        }
    }
}
