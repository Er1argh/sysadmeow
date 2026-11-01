package com.example.netadminmarketplace.data

import com.example.netadminmarketplace.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Корзина не разрешает случайно добавить одного и того же специалиста дважды. */
object CartRepository {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    fun addUnique(item: CartItem): Boolean {
        if (_items.value.any { it.specialistId == item.specialistId }) return false
        _items.value = _items.value + item
        return true
    }
    fun containsSpecialist(id: String): Boolean = _items.value.any { it.specialistId == id }
    fun remove(position: Int) { _items.value = _items.value.filterIndexed { index, _ -> index != position } }
    fun clear() { _items.value = emptyList() }
    fun total(): Long = _items.value.sumOf { it.total }
}
