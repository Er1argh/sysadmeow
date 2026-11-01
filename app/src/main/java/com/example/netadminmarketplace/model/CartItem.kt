package com.example.netadminmarketplace.model

data class CartItem(
    val specialistId: String,
    val specialistName: String,
    val serviceName: String,
    val price: Long,
    val quantity: Int = 1
) {
    val total: Long get() = price * quantity
}
