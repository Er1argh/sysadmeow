package com.example.netadminmarketplace.ui.main

import androidx.lifecycle.ViewModel
import com.example.netadminmarketplace.data.AdminRepository
import com.example.netadminmarketplace.model.Specialist
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val repository = AdminRepository()
    private var listener: ListenerRegistration? = null
    private var allAdmins: List<Pair<String, Specialist>> = emptyList()

    private val _admins = MutableStateFlow<List<Pair<String, Specialist>>>(emptyList())
    val admins: StateFlow<List<Pair<String, Specialist>>> = _admins
    private val _selectedCategory = MutableStateFlow("Все")
    val selectedCategory: StateFlow<String> = _selectedCategory
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        listener = repository.observePublished(
            onChanged = { allAdmins = it; applyFilter() },
            onError = { _error.value = it.localizedMessage ?: "Не удалось загрузить специалистов" }
        )
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        applyFilter()
    }

    private fun applyFilter() {
        val selected = _selectedCategory.value
        _admins.value = if (selected == "Все") allAdmins else allAdmins.filter { (_, admin) -> admin.categories.any { it.equals(selected, true) } }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
