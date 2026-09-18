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
    val hasSavedCredentials: Boolean = false,
    val showDialog: Boolean = false,
    val dialogTitle: String = "",
    val dialogMessage: String = ""
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
        // Prepopulate with previously saved username and password if available
        val savedUser = repository.sessionManager.getUsername()
        val savedPass = repository.sessionManager.getPassword()
        val hasSaved = repository.sessionManager.hasSavedCredentials()

        _uiState.value = _uiState.value.copy(
            username = savedUser.orEmpty(),
            password = savedPass.orEmpty(),
            hasSavedCredentials = hasSaved
        )
    }

    fun onUsernameChange(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(showDialog = false)
    }

    fun login() {
        val state = _uiState.value
        val user = state.username.trim()
        val pass = state.password.trim()

        if (user.isBlank() || pass.isBlank()) {
            _uiState.value = state.copy(
                showDialog = true,
                dialogTitle = "بيانات مطلوبة",
                dialogMessage = "يرجى إدخال اسم المستخدم وكلمة المرور للمتابعة."
            )
            return
        }

        _uiState.value = state.copy(isLoading = true, showDialog = false)

        viewModelScope.launch {
            val result = repository.login(user, pass)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, showDialog = false)
                _events.emit(LoginEvent.LoginSuccess)
            }.onFailure { error ->
                val errorMsg = error.message.orEmpty()
                val message = when {
                    errorMsg.contains("err_invalid_login", ignoreCase = true) ||
                    errorMsg.contains("invalid", ignoreCase = true) ||
                    errorMsg.contains("401") || errorMsg.contains("403") ->
                        "اسم المستخدم أو كلمة المرور غير صحيحة. يرجى التأكد من البيانات أو مراجعة الوكيل المعتمد."
                    errorMsg.contains("network", ignoreCase = true) ||
                    error is java.net.UnknownHostException || error is java.net.SocketTimeoutException ->
                        "تعذر الاتصال بخادم Skyline. يرجى التأكد من تشغيل البيانات أو الاتصال بالشبكة."
                    else -> "فشل تسجيل الدخول: ${error.localizedMessage ?: "حدث خطأ غير متوقع، يرجى المحاولة لاحقاً."}"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showDialog = true,
                    dialogTitle = "تعذر تسجيل الدخول",
                    dialogMessage = message
                )
            }
        }
    }

    fun autoLoginNetwork() {
        _uiState.value = _uiState.value.copy(isLoading = true, showDialog = false)

        viewModelScope.launch {
            // Perform background connection check to provider gateway at admin.skylineiq.com/user
            val result = repository.checkProviderConnectionAndAutoLogin()
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, showDialog = false)
                _events.emit(LoginEvent.LoginSuccess)
            }.onFailure { error ->
                val errorMsg = error.message.orEmpty()
                val message = if (errorMsg.contains("not_connected_to_provider", ignoreCase = true) ||
                    errorMsg.contains("provider_status", ignoreCase = true) ||
                    error is java.net.UnknownHostException || error is java.net.SocketTimeoutException
                ) {
                    "أنت غير متصل بمزود خدمة سكايلان حالياً (admin.skylineiq.com/user).\n\nيرجى الاتصال بشبكة واي فاي المزود للدخول التلقائي المباشر، أو قم بإدخال بيانات اشتراكك (اسم المستخدم وكلمة المرور) يدوياً."
                } else {
                    "تعذر الاتصال بالمزود: ${error.localizedMessage ?: "تأكد من الاتصال بالشبكة والمحاولة مجدداً."}"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showDialog = true,
                    dialogTitle = "تنبيه الاتصال بالمزود",
                    dialogMessage = message
                )
            }
        }
    }

    fun autoLogin() {
        if (repository.sessionManager.hasValidSession()) {
            viewModelScope.launch {
                _events.emit(LoginEvent.LoginSuccess)
            }
        } else {
            autoLoginNetwork()
        }
    }
}

