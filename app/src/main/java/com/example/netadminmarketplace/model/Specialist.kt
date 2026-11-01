package com.example.netadminmarketplace.model
/** Публичная карточка sysadmin-кота из /sysadmins/{uid}. */
data class Specialist(
    val ownerUid: String = "",
    val name: String = "",
    val avatarKey: String = "gray",
    val roleTitle: String = "",
    val categories: List<String> = emptyList(),
    val experienceYears: Long = 0,
    val rating: Double = 0.0,
    val ratingSum: Long = 0,
    val reviewsCount: Long = 0,
    val priceByService: Long = 0,
    val shortBio: String = "",
    val published: Boolean = false,
    val latestReviewText: String = "",
    val latestReviewerName: String = ""
)
