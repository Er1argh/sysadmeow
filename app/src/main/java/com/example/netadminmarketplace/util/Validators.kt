package com.example.netadminmarketplace.util
object Validators {
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    fun nameError(value: String): String? = when {
        value.trim().length < 2 -> "Введите имя не короче 2 символов"
        value.trim().length > 40 -> "Имя слишком длинное (максимум 40 символов)"
        else -> null
    }
    fun emailError(value: String): String? = when {
        value.isBlank() -> "Введите email"
        !emailRegex.matches(value.trim()) -> "Проверьте формат email, например cat@example.com"
        else -> null
    }
    fun passwordError(value: String): String? = when {
        value.length < 6 -> "Пароль должен содержать минимум 6 символов"
        value.length > 72 -> "Пароль слишком длинный"
        else -> null
    }
    fun reviewError(text: String, rating: Int): String? = when {
        rating !in 1..5 -> "Поставьте оценку от 1 до 5 лапок"
        text.trim().length < 5 -> "Напишите хотя бы 5 символов об опыте работы"
        text.trim().length > 500 -> "Отзыв не должен превышать 500 символов"
        else -> null
    }
    fun listingError(title: String, categories: List<String>, experience: Long?, price: Long?, bio: String): String? = when {
        title.trim().length < 3 -> "Название услуги должно быть не короче 3 символов"
        categories.isEmpty() -> "Выберите хотя бы одну категорию"
        experience == null || experience !in 0..60 -> "Опыт должен быть числом от 0 до 60 лет"
        price == null || price <= 0 -> "Укажите положительную стоимость услуги"
        bio.trim().length < 20 -> "Расскажите об услуге подробнее (минимум 20 символов)"
        else -> null
    }
}
