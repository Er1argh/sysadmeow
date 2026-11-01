package com.example.netadminmarketplace.data

import com.example.netadminmarketplace.model.UserProfile
import com.example.netadminmarketplace.model.UserRole
import com.example.netadminmarketplace.util.CatAvatarProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/** CRUD текущего профиля в /users/{uid}. */
class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentProfile(callback: (Result<UserProfile>) -> Unit) {
        val user = auth.currentUser ?: return callback(Result.failure(IllegalStateException("Пользователь не авторизован")))
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val loaded = doc.toObject(UserProfile::class.java)
                if (loaded != null) {
                    val key = loaded.avatarKey.takeIf { it in CatAvatarProvider.keys } ?: CatAvatarProvider.keyForUid(user.uid)
                    val profile = loaded.copy(avatarKey = key)
                    if (loaded.avatarKey != key) db.collection("users").document(user.uid).set(mapOf("avatarKey" to key), SetOptions.merge())
                    callback(Result.success(profile))
                } else {
                    val fallback = UserProfile(user.uid, user.displayName ?: "Пользователь", user.email ?: "", UserRole.CLIENT, CatAvatarProvider.keyForUid(user.uid))
                    db.collection("users").document(user.uid).set(fallback)
                    callback(Result.success(fallback))
                }
            }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    fun updateDisplayName(uid: String, name: String, callback: (Result<Unit>) -> Unit) {
        db.collection("users").document(uid).set(mapOf("displayName" to name.trim()), SetOptions.merge())
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }
}
