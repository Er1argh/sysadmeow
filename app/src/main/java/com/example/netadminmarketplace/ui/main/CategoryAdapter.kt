package com.example.netadminmarketplace.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.netadminmarketplace.R
import com.example.netadminmarketplace.databinding.ItemCategoryBinding

class CategoryAdapter(private val onSelected: (String) -> Unit) : RecyclerView.Adapter<CategoryAdapter.VH>() {
    private val items = listOf("Все", "VPN", "MikroTik", "Cisco", "Linux", "Security")
    private var selected = "Все"

    fun setSelected(value: String) {
        if (selected == value) return
        val old = items.indexOf(selected)
        selected = value
        if (old >= 0) notifyItemChanged(old)
        val new = items.indexOf(selected)
        if (new >= 0) notifyItemChanged(new)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(value: String) {
            val isSelected = value == selected
            binding.tvTitle.text = if (isSelected) "✓ $value" else value
            binding.root.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, if (isSelected) R.color.chip_selected else R.color.chip))
            binding.tvTitle.setTextColor(ContextCompat.getColor(binding.root.context, if (isSelected) R.color.white else R.color.primary))
            binding.root.setOnClickListener { onSelected(value) }
        }
    }
}
