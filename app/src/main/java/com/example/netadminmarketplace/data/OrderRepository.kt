package com.example.netadminmarketplace.data

import com.example.netadminmarketplace.model.CartItem
import com.example.netadminmarketplace.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

object OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    fun createPaidOrder(items: List<CartItem>, callback: (Result<Order>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(Result.failure(IllegalStateException("Нет авторизованного пользователя")))
        if (items.isEmpty()) return callback(Result.failure(IllegalStateException("Корзина пуста")))
        val order = Order(UUID.randomUUID().toString().take(8).uppercase(), uid, items, items.sumOf { it.total }, "PAID")
        val data = mapOf(
            "id" to order.id, "userId" to uid,
            "specialistIds" to items.map { it.specialistId },
            "items" to items.map { mapOf("specialistId" to it.specialistId, "specialistName" to it.specialistName, "serviceName" to it.serviceName, "price" to it.price, "quantity" to it.quantity) },
            "total" to order.total, "status" to order.status, "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("orders").document(order.id).set(data)
            .addOnSuccessListener { callback(Result.success(order)) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }
}
