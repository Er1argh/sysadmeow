package com.example.netadminmarketplace.model

data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val total: Long,
    val status: String
)
