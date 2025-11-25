package week11.st910491.finalproject.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st910491.finalproject.data.auth.AuthRepository

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState

    private val _registerState = MutableStateFlow(AuthUiState())
    val registerState: StateFlow<AuthUiState> = _registerState

    private val _forgotState = MutableStateFlow(AuthUiState())
    val forgotState: StateFlow<AuthUiState> = _forgotState

    // ---------- LOGIN ----------

    fun onLoginEmailChange(value: String) {
        _loginState.value = _loginState.value.copy(email = value, errorMessage = null)
    }

    fun onLoginPasswordChange(value: String) {
        _loginState.value = _loginState.value.copy(password = value, errorMessage = null)
    }

    fun login() {
        val state = _loginState.value
        if (state.email.isBlank() || state.password.length < 6) {
            _loginState.value = state.copy(
                errorMessage = "Enter a valid email and password (min 6 characters)."
            )
            return
        }

        viewModelScope.launch {
            _loginState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.login(state.email, state.password)
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Login failed."
                )
            }
        }
    }

    fun resetLoginSuccess() {
        _loginState.value = _loginState.value.copy(isSuccess = false)
    }

    // ---------- REGISTER ----------

    fun onRegisterEmailChange(value: String) {
        _registerState.value = _registerState.value.copy(email = value, errorMessage = null)
    }

    fun onRegisterPasswordChange(value: String) {
        _registerState.value = _registerState.value.copy(password = value, errorMessage = null)
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        _registerState.value =
            _registerState.value.copy(confirmPassword = value, errorMessage = null)
    }

    fun register() {
        val state = _registerState.value
        if (state.email.isBlank() || state.password.length < 6) {
            _registerState.value = state.copy(
                errorMessage = "Password must be at least 6 characters."
            )
            return
        }
        if (state.password != state.confirmPassword) {
            _registerState.value = state.copy(
                errorMessage = "Passwords do not match."
            )
            return
        }

        viewModelScope.launch {
            _registerState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.register(state.email, state.password)
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Registration failed."
                )
            }
        }
    }

    fun resetRegisterSuccess() {
        _registerState.value = _registerState.value.copy(isSuccess = false)
    }

    // ---------- FORGOT PASSWORD ----------

    fun onForgotEmailChange(value: String) {
        _forgotState.value = _forgotState.value.copy(email = value, errorMessage = null)
    }

    fun sendResetEmail() {
        val state = _forgotState.value
        if (state.email.isBlank()) {
            _forgotState.value = state.copy(errorMessage = "Email cannot be empty.")
            return
        }

        viewModelScope.launch {
            _forgotState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.sendPasswordReset(state.email)
                _forgotState.value = _forgotState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _forgotState.value = _forgotState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to send reset email."
                )
            }
        }
    }
}
