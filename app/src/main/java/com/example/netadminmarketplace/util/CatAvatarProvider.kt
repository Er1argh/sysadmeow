package com.example.netadminmarketplace.util
import com.example.netadminmarketplace.R
import kotlin.math.absoluteValue
object CatAvatarProvider {
    val keys = listOf("ginger", "gray", "cream", "blue", "green", "pink")
    fun randomKey(): String = keys.random()
    fun keyForUid(uid: String): String = keys[uid.hashCode().absoluteValue % keys.size]
    fun drawableFor(key: String): Int = when (key) {
        "ginger" -> R.drawable.avatar_cat_ginger
        "cream" -> R.drawable.avatar_cat_cream
        "blue" -> R.drawable.avatar_cat_blue
        "green" -> R.drawable.avatar_cat_green
        "pink" -> R.drawable.avatar_cat_pink
        else -> R.drawable.avatar_cat_gray
    }
}
