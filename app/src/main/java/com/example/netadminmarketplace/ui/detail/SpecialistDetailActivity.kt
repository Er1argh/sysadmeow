package com.example.netadminmarketplace.ui.detail
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.netadminmarketplace.data.CartRepository
import com.example.netadminmarketplace.databinding.ActivitySpecialistDetailBinding
import com.example.netadminmarketplace.model.CartItem
import com.example.netadminmarketplace.util.CatAvatarProvider
import com.example.netadminmarketplace.util.Validators
import kotlinx.coroutines.launch
class SpecialistDetailActivity:AppCompatActivity(){
    private lateinit var b:ActivitySpecialistDetailBinding;private val vm:SpecialistDetailViewModel by viewModels();private var id="";private var addLocked=false
    override fun onCreate(s:Bundle?){super.onCreate(s);b=ActivitySpecialistDetailBinding.inflate(layoutInflater);setContentView(b.root);b.root.isFocusableInTouchMode=true;b.root.requestFocus();id=intent.getStringExtra("adminId").orEmpty();if(id.isBlank()){finish();return};b.rvReviews.layoutManager=LinearLayoutManager(this);val adapter=ReviewAdapter();b.rvReviews.adapter=adapter;b.btnBack.setOnClickListener{finish()};b.btnPrev.setOnClickListener{vm.previousPage()};b.btnNext.setOnClickListener{vm.nextPage()};b.btnReview.setOnClickListener{val rating=b.ratingBar.rating.toInt();val text=b.etReview.text.toString();val err=Validators.reviewError(text,rating);if(err!=null){b.tvReviewValidation.text=err;b.tvReviewValidation.visibility=View.VISIBLE}else{b.tvReviewValidation.visibility=View.GONE;vm.submitReview(rating,text)}};b.btnAdd.setOnClickListener{addToCart()};vm.load(id)
        lifecycleScope.launch{vm.specialist.collect{spt->if(spt!=null){b.ivAvatar.setImageResource(CatAvatarProvider.drawableFor(spt.avatarKey));b.tvName.text=spt.name;b.tvRole.text=spt.roleTitle;b.tvMeta.text="${spt.experienceYears} лет опыта · ${if(spt.reviewsCount==0L)"нет оценок" else "★ %.1f (%d)".format(spt.rating,spt.reviewsCount)}";b.tvStack.text=spt.categories.joinToString(" • ");b.tvBio.text=spt.shortBio;b.tvPrice.text="${spt.priceByService} BYN за разовую настройку";updateCartButton()}}}
        lifecycleScope.launch{vm.reviews.collect{adapter.submit(it);b.tvNoReviews.visibility=if(it.isEmpty())View.VISIBLE else View.GONE}}
        lifecycleScope.launch{vm.pageState.collect{p->b.tvPage.text="Страница ${p.pageNumber}";b.btnPrev.isEnabled=p.canPrevious&&!p.loading;b.btnNext.isEnabled=p.canNext&&!p.loading;b.reviewProgress.visibility=if(p.loading)View.VISIBLE else View.GONE}}
        lifecycleScope.launch{vm.reviewState.collect{renderReviewState(it)}}
    }
    override fun onResume(){super.onResume();addLocked=false;updateCartButton()}
    private fun addToCart(){if(addLocked)return;val s=vm.specialist.value?:return;addLocked=true;val added=CartRepository.addUnique(CartItem(id,s.name,"Разовая настройка сети",s.priceByService));Toast.makeText(this,if(added)"Услуга добавлена в корзину" else "Этот sysadmin-кот уже в корзине",Toast.LENGTH_SHORT).show();updateCartButton();addLocked=false}
    private fun updateCartButton(){val exists=id.isNotBlank()&&CartRepository.containsSpecialist(id);b.btnAdd.isEnabled=!exists;b.btnAdd.text=if(exists)"Уже в корзине" else "Добавить услугу в корзину"}
    private fun renderReviewState(s:ReviewUiState){when(s){ReviewUiState.Loading->{b.reviewForm.visibility=View.GONE;b.tvReviewHint.text="Проверяем, можно ли оставить отзыв…"};ReviewUiState.Ready->{b.reviewForm.visibility=View.VISIBLE;b.btnReview.isEnabled=true;b.tvReviewHint.text="Поделитесь опытом: оценка влияет на общий рейтинг кота."};ReviewUiState.Submitting->{b.btnReview.isEnabled=false;b.tvReviewHint.text="Публикуем отзыв…"};is ReviewUiState.Blocked->{b.reviewForm.visibility=View.GONE;b.tvReviewHint.text=s.message};is ReviewUiState.Success->{b.reviewForm.visibility=View.GONE;b.tvReviewHint.text=s.message};is ReviewUiState.Error->{b.btnReview.isEnabled=true;b.tvReviewHint.text=s.message}}}
}
