package com.example.netadminmarketplace.ui.detail
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.netadminmarketplace.databinding.ItemReviewBinding
import com.example.netadminmarketplace.model.Review
import com.example.netadminmarketplace.util.CatAvatarProvider
class ReviewAdapter: RecyclerView.Adapter<ReviewAdapter.VH>() {
    private val items=mutableListOf<Review>(); fun submit(v:List<Review>){items.clear();items.addAll(v);notifyDataSetChanged()}
    override fun onCreateViewHolder(p:ViewGroup,v:Int)=VH(ItemReviewBinding.inflate(LayoutInflater.from(p.context),p,false)); override fun getItemCount()=items.size; override fun onBindViewHolder(h:VH,p:Int)=h.bind(items[p])
    class VH(private val b:ItemReviewBinding):RecyclerView.ViewHolder(b.root){fun bind(r:Review){b.ivAvatar.setImageResource(CatAvatarProvider.drawableFor(r.authorAvatarKey));b.tvAuthor.text=r.authorName;b.tvRating.text="★".repeat(r.rating.toInt().coerceIn(1,5));b.tvText.text=r.text;b.tvDate.text=r.createdAt?.toDate()?.let{java.text.SimpleDateFormat("dd.MM.yyyy",java.util.Locale.getDefault()).format(it)}?:"только что"}}
}
