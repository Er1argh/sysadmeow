package com.example.netadminmarketplace.model
/** Профиль Firebase-пользователя. avatarKey выбирается один раз при регистрации и хранится в Firestore. */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = UserRole.CLIENT,
    val avatarKey: String = "gray"
)
