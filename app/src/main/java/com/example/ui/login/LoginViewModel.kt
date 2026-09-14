package com.example.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.SkylineRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginEvent {
    data object LoginSuccess : LoginEvent
}

class LoginViewModel(
    private val repository: SkylineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    init {
        // Prepopulate with previously saved username if available
        val savedUser = repository.sessionManager.getUsername()
        if (!savedUser.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(username = savedUser)
        }
    }

    fun onUsernameChange(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername, errorMessage = null)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword, errorMessage = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "يرجى إدخال اسم المستخدم وكلمة المرور")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.login(state.username, state.password)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
                _events.emit(LoginEvent.LoginSuccess)
            }.onFailure { error ->
                val message = when {
                    error.message?.contains("err_invalid_login", ignoreCase = true) == true ->
                        "اسم المستخدم أو كلمة المرور غير صحيحة"
                    error.message?.contains("network", ignoreCase = true) == true ||
                    error is java.net.UnknownHostException || error is java.net.SocketTimeoutException ->
                        "تعذر الاتصال بالخادم، يرجى التأكد من اتصال الإنترنت"
                    else -> "فشل تسجيل الدخول: ${error.localizedMessage ?: "خطأ غير معروف"}"
                }
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
            }
        }
    }

    fun autoLogin() {
        if (repository.sessionManager.hasValidSession()) {
            viewModelScope.launch {
                _events.emit(LoginEvent.LoginSuccess)
            }
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = "لا توجد جلسة محفوظة سابقة، يرجى تسجيل الدخول بكلمة المرور أولاً"
            )
        }
    }
}
