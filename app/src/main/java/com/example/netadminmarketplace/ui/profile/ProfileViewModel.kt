package com.example.netadminmarketplace.ui.profile
import androidx.lifecycle.ViewModel
import com.example.netadminmarketplace.data.AdminRepository
import com.example.netadminmarketplace.data.AuthRepository
import com.example.netadminmarketplace.data.UserRepository
import com.example.netadminmarketplace.model.Specialist
import com.example.netadminmarketplace.model.UserProfile
import com.example.netadminmarketplace.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class ProfileViewModel:ViewModel(){
    private val users=UserRepository();private val admins=AdminRepository();private val auth=AuthRepository();private val _profile=MutableStateFlow<UserProfile?>(null);val profile:StateFlow<UserProfile?> = _profile;private val _listing=MutableStateFlow<Specialist?>(null);val listing:StateFlow<Specialist?> = _listing;private val _message=MutableStateFlow<String?>(null);val message:StateFlow<String?> = _message
    init{refresh()}
    fun refresh(){users.getCurrentProfile{r->r.onSuccess{p->_profile.value=p;if(p.role==UserRole.ADMIN_CAT)admins.getMyListing(p.uid){x->x.onSuccess{_listing.value=it}.onFailure{_message.value=it.localizedMessage}}}.onFailure{_message.value=it.localizedMessage}}}
    fun saveName(name:String){val p=_profile.value?:return;users.updateDisplayName(p.uid,name){r->r.onSuccess{val updated=p.copy(displayName=name.trim());_profile.value=updated;if(updated.role==UserRole.ADMIN_CAT)admins.saveListing(updated.uid,mapOf("name" to updated.displayName)){z->z.onSuccess{_listing.value=_listing.value?.copy(name=updated.displayName);_message.value="Имя профиля сохранено"}.onFailure{_message.value=it.localizedMessage}}else _message.value="Имя профиля сохранено"}.onFailure{_message.value=it.localizedMessage}}}
    fun saveListing(title:String,categories:List<String>,experience:Long,price:Long,bio:String,published:Boolean){val p=_profile.value?:return;if(p.role!=UserRole.ADMIN_CAT){_message.value="Только sysadmin-кот может публиковать карточку";return};val c=_listing.value;val data=mapOf<String,Any>("ownerUid" to p.uid,"name" to p.displayName,"avatarKey" to p.avatarKey,"roleTitle" to title.trim(),"categories" to categories,"experienceYears" to experience,"priceByService" to price,"shortBio" to bio.trim(),"published" to published,"rating" to(c?.rating?:0.0),"ratingSum" to(c?.ratingSum?:0L),"reviewsCount" to(c?.reviewsCount?:0L),"latestReviewText" to(c?.latestReviewText.orEmpty()),"latestReviewerName" to(c?.latestReviewerName.orEmpty()));admins.saveListing(p.uid,data){r->r.onSuccess{_listing.value=Specialist(ownerUid=p.uid,name=p.displayName,avatarKey=p.avatarKey,roleTitle=title.trim(),categories=categories,experienceYears=experience,rating=c?.rating?:0.0,ratingSum=c?.ratingSum?:0L,reviewsCount=c?.reviewsCount?:0L,priceByService=price,shortBio=bio.trim(),published=published,latestReviewText=c?.latestReviewText.orEmpty(),latestReviewerName=c?.latestReviewerName.orEmpty());_message.value=if(published)"Карточка опубликована в каталоге" else "Черновик сохранён"}.onFailure{_message.value=it.localizedMessage}}}
    fun consumeMessage(){_message.value=null};fun logout()=auth.signOut()
}
