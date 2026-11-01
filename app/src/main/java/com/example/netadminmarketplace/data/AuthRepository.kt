package com.example.netadminmarketplace.data

import com.example.netadminmarketplace.model.UserRole
import com.example.netadminmarketplace.util.CatAvatarProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/** Firebase Authentication + создание пользовательского профиля и черновика sysadmin-кота. */
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val currentUser get() = auth.currentUser

    fun login(email: String, password: String, callback: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    fun register(displayName: String, email: String, password: String, role: String, avatarKey: String, callback: (Result<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener callback(Result.failure(IllegalStateException("Firebase не вернул UID")))
                val key = avatarKey.takeIf { it in CatAvatarProvider.keys } ?: CatAvatarProvider.keyForUid(uid)
                val profile = mapOf(
                    "uid" to uid,
                    "displayName" to displayName.trim(),
                    "email" to email.trim(),
                    "role" to role,
                    "avatarKey" to key
                )
                db.collection("users").document(uid).set(profile)
                    .addOnSuccessListener {
                        if (role == UserRole.ADMIN_CAT) createDraftSysadmin(uid, displayName.trim(), key, callback)
                        else callback(Result.success(role))
                    }
                    .addOnFailureListener { callback(Result.failure(it)) }
            }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    private fun createDraftSysadmin(uid: String, name: String, avatarKey: String, callback: (Result<String>) -> Unit) {
        val draft = mapOf(
            "ownerUid" to uid,
            "name" to name,
            "avatarKey" to avatarKey,
            "roleTitle" to "",
            "categories" to emptyList<String>(),
            "experienceYears" to 0L,
            "rating" to 0.0,
            "ratingSum" to 0L,
            "reviewsCount" to 0L,
            "priceByService" to 0L,
            "shortBio" to "",
            "published" to false,
            "latestReviewText" to "",
            "latestReviewerName" to ""
        )
        db.collection("sysadmins").document(uid).set(draft)
            .addOnSuccessListener { callback(Result.success(UserRole.ADMIN_CAT)) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    fun signOut() = auth.signOut()
}
