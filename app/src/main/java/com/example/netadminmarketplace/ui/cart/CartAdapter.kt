package com.example.netadminmarketplace.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.netadminmarketplace.databinding.ItemCartBinding
import com.example.netadminmarketplace.model.CartItem

class CartAdapter(private val onRemove: (Int) -> Unit) : RecyclerView.Adapter<CartAdapter.VH>() {
    private val items = mutableListOf<CartItem>()
    fun submit(values: List<CartItem>) { items.clear(); items.addAll(values); notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position], position)
    inner class VH(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem, position: Int) {
            binding.tvTitle.text = item.serviceName
            binding.tvSubtitle.text = item.specialistName
            binding.tvPrice.text = "${item.total} BYN"
            binding.btnRemove.setOnClickListener { onRemove(position) }
        }
    }
}
