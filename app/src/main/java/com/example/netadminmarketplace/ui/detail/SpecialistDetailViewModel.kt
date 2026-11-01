package com.example.netadminmarketplace.ui.detail
import androidx.lifecycle.ViewModel
import com.example.netadminmarketplace.data.AdminRepository
import com.example.netadminmarketplace.data.ReviewRepository
import com.example.netadminmarketplace.data.UserRepository
import com.example.netadminmarketplace.model.Review
import com.example.netadminmarketplace.model.Specialist
import com.example.netadminmarketplace.model.UserProfile
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class SpecialistDetailViewModel : ViewModel() {
    private val admins=AdminRepository(); private val reviewRepo=ReviewRepository(); private val users=UserRepository()
    private val _specialist=MutableStateFlow<Specialist?>(null); val specialist:StateFlow<Specialist?> = _specialist
    private val _reviews=MutableStateFlow<List<Review>>(emptyList()); val reviews:StateFlow<List<Review>> = _reviews
    private val _pageState=MutableStateFlow(PageState()); val pageState:StateFlow<PageState> = _pageState
    private val _reviewState=MutableStateFlow<ReviewUiState>(ReviewUiState.Loading); val reviewState:StateFlow<ReviewUiState> = _reviewState
    private var adminId=""; private var currentUser:UserProfile?=null; private val cache=mutableListOf<ReviewRepository.Page>(); private var index=0
    fun load(id:String) { if(adminId==id && _specialist.value!=null) return; adminId=id; refreshSpecialist(); loadFirstPage(); users.getCurrentProfile { r -> r.onSuccess { currentUser=it; checkEligibility() }.onFailure { _reviewState.value=ReviewUiState.Error(it.localizedMessage?:"Не удалось загрузить профиль") } } }
    private fun refreshSpecialist() { admins.getAdmin(adminId) { r -> r.onSuccess { _specialist.value=it }.onFailure { _pageState.value=_pageState.value.copy(error=it.localizedMessage) } } }
    private fun checkEligibility() { val u=currentUser?:return; if(u.uid==adminId){_reviewState.value=ReviewUiState.Blocked("Это ваша карточка. Оценивать самого себя нельзя.");return}; reviewRepo.hasReview(adminId,u.uid){r->r.onSuccess{exists->_reviewState.value=if(exists) ReviewUiState.Blocked("Вы уже оставили отзыв этому sysadmin-коту.") else ReviewUiState.Ready}.onFailure{_reviewState.value=ReviewUiState.Error(it.localizedMessage?:"Не удалось проверить отзыв")}} }
    fun submitReview(rating:Int,text:String){ val u=currentUser?:return; if(_reviewState.value !is ReviewUiState.Ready)return; _reviewState.value=ReviewUiState.Submitting; reviewRepo.submitReview(adminId,u,rating,text){r->r.onSuccess{_reviewState.value=ReviewUiState.Success("Спасибо! Отзыв опубликован, рейтинг пересчитан.");refreshSpecialist();loadFirstPage()}.onFailure{_reviewState.value=ReviewUiState.Error(it.localizedMessage?:"Не удалось сохранить отзыв")}} }
    fun loadFirstPage(){if(adminId.isBlank())return;cache.clear();index=0;loadPage(null,0)}
    fun nextPage(){val s=_pageState.value;if(!s.canNext||s.loading)return;val target=index+1;if(target<cache.size){index=target;showCached();return};val cursor=cache.getOrNull(index)?.nextCursor?:return;loadPage(cursor,target)}
    fun previousPage(){if(index<=0||_pageState.value.loading)return;index--;showCached()}
    private fun loadPage(cursor:DocumentSnapshot?,target:Int){_pageState.value=_pageState.value.copy(loading=true,error=null);reviewRepo.loadPage(adminId,cursor){r->r.onSuccess{p->index=target;if(cache.size<=target)cache.add(p)else cache[target]=p;_reviews.value=p.reviews;_pageState.value=PageState(index+1,index>0,p.hasNext||index+1<cache.size,false,null)}.onFailure{_pageState.value=_pageState.value.copy(loading=false,error=it.localizedMessage)}}}
    private fun showCached(){val p=cache[index];_reviews.value=p.reviews;_pageState.value=PageState(index+1,index>0,p.hasNext||index+1<cache.size,false,null)}
}
data class PageState(val pageNumber:Int=1,val canPrevious:Boolean=false,val canNext:Boolean=false,val loading:Boolean=false,val error:String?=null)
sealed class ReviewUiState { data object Loading:ReviewUiState(); data object Ready:ReviewUiState(); data object Submitting:ReviewUiState(); data class Success(val message:String):ReviewUiState(); data class Blocked(val message:String):ReviewUiState(); data class Error(val message:String):ReviewUiState() }
