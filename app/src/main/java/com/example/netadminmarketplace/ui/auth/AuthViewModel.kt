package com.example.netadminmarketplace.ui.auth
import androidx.lifecycle.ViewModel
import com.example.netadminmarketplace.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state
    fun login(email: String, password: String) {
        _state.value = AuthState.Loading
        repository.login(email, password) { result ->
            _state.value = result.fold({ AuthState.Success(null) }, { AuthState.Error(friendlyAuthError(it)) })
        }
    }
    fun register(name: String, email: String, password: String, role: String, avatarKey: String) {
        _state.value = AuthState.Loading
        repository.register(name, email, password, role, avatarKey) { result ->
            _state.value = result.fold({ AuthState.Success(it) }, { AuthState.Error(friendlyAuthError(it)) })
        }
    }
    fun resetError() { if (_state.value is AuthState.Error) _state.value = AuthState.Idle }
    private fun friendlyAuthError(error: Throwable): String {
        val raw = error.localizedMessage.orEmpty()
        return when {
            raw.contains("password", true) && raw.contains("invalid", true) -> "Неверный пароль. Проверьте ввод и попробуйте снова."
            raw.contains("no user", true) || raw.contains("user record", true) -> "Аккаунт с таким email не найден."
            raw.contains("email address", true) && raw.contains("already", true) -> "Этот email уже зарегистрирован. Попробуйте войти."
            raw.contains("network", true) -> "Нет соединения с Firebase. Проверьте интернет."
            raw.isNotBlank() -> raw
            else -> "Не удалось выполнить операцию. Попробуйте ещё раз."
        }
    }
}
sealed class AuthState {
    data object Idle : AuthState(); data object Loading : AuthState()
    data class Success(val role: String?) : AuthState()
    data class Error(val message: String) : AuthState()
}
