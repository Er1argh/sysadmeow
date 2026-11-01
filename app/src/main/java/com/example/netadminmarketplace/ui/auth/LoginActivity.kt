package com.example.netadminmarketplace.ui.auth
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.netadminmarketplace.databinding.ActivityLoginBinding
import com.example.netadminmarketplace.ui.main.MainActivity
import com.example.netadminmarketplace.util.Validators
import kotlinx.coroutines.launch
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private var navigated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); binding = ActivityLoginBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.root.isFocusableInTouchMode = true; binding.root.requestFocus()
        binding.btnLogin.setOnClickListener { submit() }
        binding.btnRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        lifecycleScope.launch { viewModel.state.collect { render(it) } }
    }
    private fun submit() {
        val email = binding.etEmail.text.toString(); val password = binding.etPassword.text.toString()
        val emailError = Validators.emailError(email); val passwordError = Validators.passwordError(password)
        binding.tvEmailError.text = emailError.orEmpty(); binding.tvEmailError.visibility = if (emailError == null) View.GONE else View.VISIBLE
        binding.tvPasswordError.text = passwordError.orEmpty(); binding.tvPasswordError.visibility = if (passwordError == null) View.GONE else View.VISIBLE
        binding.tvFormError.visibility = View.GONE
        if (emailError != null || passwordError != null) return
        viewModel.login(email, password)
    }
    private fun render(state: AuthState) {
        val loading = state is AuthState.Loading
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading; binding.btnRegister.isEnabled = !loading
        when (state) {
            is AuthState.Error -> { binding.tvFormError.text = state.message; binding.tvFormError.visibility = View.VISIBLE }
            is AuthState.Success -> if (!navigated) { navigated = true; startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish() }
            else -> Unit
        }
    }
}
