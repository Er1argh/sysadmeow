package com.example.netadminmarketplace.ui.cart

import androidx.lifecycle.ViewModel
import com.example.netadminmarketplace.data.CartRepository
import kotlinx.coroutines.flow.map

class CartViewModel : ViewModel() {
    val items = CartRepository.items
    val total = CartRepository.items.map { items -> items.sumOf { it.total } }
    fun removeAt(position: Int) = CartRepository.remove(position)
}
