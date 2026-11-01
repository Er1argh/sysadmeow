package com.example.netadminmarketplace.ui.auth
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.netadminmarketplace.databinding.ActivityRegisterBinding
import com.example.netadminmarketplace.model.UserRole
import com.example.netadminmarketplace.ui.main.MainActivity
import com.example.netadminmarketplace.ui.profile.ProfileActivity
import com.example.netadminmarketplace.util.CatAvatarProvider
import com.example.netadminmarketplace.util.Validators
import kotlinx.coroutines.launch
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private var avatarKey = CatAvatarProvider.randomKey()
    private var navigated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); binding = ActivityRegisterBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.root.isFocusableInTouchMode = true; binding.root.requestFocus(); renderAvatar()
        binding.btnAvatar.setOnClickListener { avatarKey = CatAvatarProvider.randomKey(); renderAvatar() }
        binding.btnBack.setOnClickListener { finish() }
        binding.rgRole.setOnCheckedChangeListener { _, checked ->
            binding.tvRoleHint.text = if (checked == binding.rbAdmin.id) "Вы сможете создать публичную карточку sysadmin-кота в профиле." else "Вы сможете искать специалистов, оплачивать услуги и оставлять отзывы."
        }
        binding.btnRegister.setOnClickListener { submit() }
        lifecycleScope.launch { viewModel.state.collect { renderState(it) } }
    }
    private fun renderAvatar() { binding.ivAvatar.setImageResource(CatAvatarProvider.drawableFor(avatarKey)) }
    private fun submit() {
        val name = binding.etName.text.toString(); val email = binding.etEmail.text.toString(); val password = binding.etPassword.text.toString()
        val n = Validators.nameError(name); val e = Validators.emailError(email); val p = Validators.passwordError(password)
        show(binding.tvNameError, n); show(binding.tvEmailError, e); show(binding.tvPasswordError, p); binding.tvFormError.visibility = View.GONE
        if (n != null || e != null || p != null) return
        val role = if (binding.rbAdmin.isChecked) UserRole.ADMIN_CAT else UserRole.CLIENT
        viewModel.register(name, email, password, role, avatarKey)
    }
    private fun show(view: android.widget.TextView, error: String?) { view.text = error.orEmpty(); view.visibility = if (error == null) View.GONE else View.VISIBLE }
    private fun renderState(state: AuthState) {
        val loading = state is AuthState.Loading; binding.progress.visibility = if (loading) View.VISIBLE else View.GONE; binding.btnRegister.isEnabled = !loading
        when(state) {
            is AuthState.Error -> { binding.tvFormError.text = state.message; binding.tvFormError.visibility = View.VISIBLE }
            is AuthState.Success -> if (!navigated) { navigated = true; val target = if (state.role == UserRole.ADMIN_CAT) ProfileActivity::class.java else MainActivity::class.java; startActivity(Intent(this,target).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish() }
            else -> Unit
        }
    }
}
