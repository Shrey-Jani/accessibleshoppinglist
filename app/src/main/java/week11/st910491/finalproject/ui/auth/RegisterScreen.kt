package week11.st910491.finalproject.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val state by viewModel.registerState.collectAsState()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    // --- SHARED STYLING CONSTANTS ---
    val primaryColor = Color(0xFF3F51B5) // Deep Indigo
    val navyTextColor = Color(0xFF1A237E) // Dark Navy
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD), // Light Blue
            Color(0xFFF3E5F5)  // Light Purple/White
        )
    )

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Added verticalScroll because registration forms can get long on small screens
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Spacer for top padding when scrolling
                Spacer(modifier = Modifier.height(20.dp))

                // --- 1. HEADER ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = "Create Account Icon",
                        modifier = Modifier.size(90.dp),
                        tint = primaryColor
                    )

                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = navyTextColor,
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = "Sign up to start managing your shopping list effectively.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                // --- 2. INPUT FIELDS ---
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Email Field
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onRegisterEmailChange,
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Gray)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = primaryColor
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = viewModel::onRegisterPasswordChange,
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray)
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = primaryColor
                        )
                    )

                    // Confirm Password Field
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = viewModel::onRegisterConfirmPasswordChange,
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray)
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle confirm password visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = primaryColor
                        )
                    )
                }

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                // --- 3. ACTION BUTTONS ---
                Button(
                    enabled = !state.isLoading,
                    onClick = { viewModel.register() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = "Create Account",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Using TextButton instead of OutlinedButton for the "Back" action
                // allows the primary "Create" button to stand out more.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    TextButton(onClick = onBackToLogin) {
                        Text(
                            text = "Login",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Spacer for bottom padding when scrolling
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}