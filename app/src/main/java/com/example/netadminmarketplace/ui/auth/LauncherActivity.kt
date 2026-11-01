package com.example.netadminmarketplace.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.netadminmarketplace.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

/** Сохраняет сессию: FirebaseAuth.currentUser остается авторизованным до signOut(). */
class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val target = if (FirebaseAuth.getInstance().currentUser == null) LoginActivity::class.java else MainActivity::class.java
        startActivity(Intent(this, target))
        finish()
    }
}
