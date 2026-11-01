package com.example.netadminmarketplace.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.netadminmarketplace.databinding.ActivityCartBinding
import com.example.netadminmarketplace.ui.checkout.CheckoutActivity
import com.example.netadminmarketplace.ui.common.BottomNav
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels()
    private val adapter = CartAdapter { viewModel.removeAt(it) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.rvCart.layoutManager = LinearLayoutManager(this); binding.rvCart.adapter = adapter
        BottomNav.bind(this, binding.bottomNav.navHome, binding.bottomNav.navCart, binding.bottomNav.navProfile, BottomNav.Current.CART)
        binding.btnCheckout.setOnClickListener { if (adapter.itemCount > 0) startActivity(Intent(this, CheckoutActivity::class.java)) }
        lifecycleScope.launch { viewModel.items.collect { adapter.submit(it); binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE; binding.btnCheckout.isEnabled = it.isNotEmpty() } }
        lifecycleScope.launch { viewModel.total.collect { binding.tvTotal.text = "Итого: $it BYN" } }
    }
}
