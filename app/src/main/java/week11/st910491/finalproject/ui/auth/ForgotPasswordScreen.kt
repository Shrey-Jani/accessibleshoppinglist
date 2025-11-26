package week11.st910491.finalproject.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Reset password",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Enter your email address and we will send you a reset link.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onForgotEmailChange,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                enabled = !state.isLoading,
                onClick = { viewModel.sendResetEmail() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
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
