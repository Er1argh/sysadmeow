package com.example.netadminmarketplace.ui.common

import android.app.Activity
import android.content.Intent
import android.widget.TextView
import com.example.netadminmarketplace.R
import com.example.netadminmarketplace.ui.cart.CartActivity
import com.example.netadminmarketplace.ui.main.MainActivity
import com.example.netadminmarketplace.ui.profile.ProfileActivity

object BottomNav {
    enum class Current { HOME, CART, PROFILE }

    fun bind(activity: Activity, home: TextView, cart: TextView, profile: TextView, current: Current) {
        val views = listOf(home to Current.HOME, cart to Current.CART, profile to Current.PROFILE)
        views.forEach { (view, item) ->
            view.setBackgroundResource(if (item == current) R.drawable.bg_nav_selected else R.drawable.bg_nav_unselected)
        }
        home.setOnClickListener { if (current != Current.HOME) activity.startActivity(Intent(activity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)) }
        cart.setOnClickListener { if (current != Current.CART) activity.startActivity(Intent(activity, CartActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)) }
        profile.setOnClickListener { if (current != Current.PROFILE) activity.startActivity(Intent(activity, ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)) }
    }
}
