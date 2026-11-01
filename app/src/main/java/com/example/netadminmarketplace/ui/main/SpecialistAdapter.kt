package com.example.netadminmarketplace.ui.main
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.netadminmarketplace.databinding.ItemSpecialistBinding
import com.example.netadminmarketplace.model.Specialist
import com.example.netadminmarketplace.util.CatAvatarProvider
class SpecialistAdapter(private val onClick: (String) -> Unit) : RecyclerView.Adapter<SpecialistAdapter.VH>() {
    private val items = mutableListOf<Pair<String, Specialist>>()
    fun submit(value: List<Pair<String, Specialist>>) { items.clear(); items.addAll(value); notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSpecialistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    inner class VH(private val b: ItemSpecialistBinding): RecyclerView.ViewHolder(b.root) {
        fun bind(pair: Pair<String, Specialist>) { val (id,s)=pair
            b.ivAvatar.setImageResource(CatAvatarProvider.drawableFor(s.avatarKey)); b.tvName.text=s.name; b.tvRole.text=s.roleTitle.ifBlank { "Sysadmin-кот" }
            b.tvStack.text=s.categories.joinToString(" • "); b.tvMeta.text="${s.experienceYears} лет опыта · ${if(s.reviewsCount==0L) "нет оценок" else "★ %.1f · %d отзывов".format(s.rating,s.reviewsCount)}"
            b.tvPrice.text="от ${s.priceByService} BYN"
            if(s.latestReviewText.isBlank()) { b.tvLatestReview.text="Пока без отзывов — можете стать первым" } else b.tvLatestReview.text="«${s.latestReviewText}» — ${s.latestReviewerName}"
            b.root.setOnClickListener { if (bindingAdapterPosition != RecyclerView.NO_POSITION) onClick(id) }
        }
    }
}
