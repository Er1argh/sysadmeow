package com.example.netadminmarketplace.model
import com.google.firebase.Timestamp
/** Один пользователь может создать только один отзыв одному sysadmin-коту. */
data class Review(
    val authorUid: String = "",
    val authorName: String = "",
    val authorAvatarKey: String = "gray",
    val rating: Long = 0,
    val text: String = "",
    val createdAt: Timestamp? = null
)
