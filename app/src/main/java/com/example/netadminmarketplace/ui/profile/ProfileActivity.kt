package com.example.netadminmarketplace.ui.profile
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.netadminmarketplace.databinding.ActivityProfileBinding
import com.example.netadminmarketplace.model.UserRole
import com.example.netadminmarketplace.ui.auth.LoginActivity
import com.example.netadminmarketplace.ui.common.BottomNav
import com.example.netadminmarketplace.util.CatAvatarProvider
import com.example.netadminmarketplace.util.Validators
import kotlinx.coroutines.launch
class ProfileActivity:AppCompatActivity(){private lateinit var b:ActivityProfileBinding;private val vm:ProfileViewModel by viewModels();private var filled=false
    override fun onCreate(s:Bundle?){super.onCreate(s);b=ActivityProfileBinding.inflate(layoutInflater);setContentView(b.root);b.root.isFocusableInTouchMode=true;b.root.requestFocus();BottomNav.bind(this,b.bottomNav.navHome,b.bottomNav.navCart,b.bottomNav.navProfile,BottomNav.Current.PROFILE);b.btnSaveName.setOnClickListener{val err=Validators.nameError(b.etName.text.toString());showError(err);if(err==null)vm.saveName(b.etName.text.toString())};b.btnSaveDraft.setOnClickListener{saveListing(false)};b.btnPublish.setOnClickListener{saveListing(true)};b.btnLogout.setOnClickListener{vm.logout();startActivity(Intent(this,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK));finish()}
        lifecycleScope.launch{vm.profile.collect{p->if(p!=null){b.ivAvatar.setImageResource(CatAvatarProvider.drawableFor(p.avatarKey));b.tvEmail.text=p.email;b.tvRole.text=if(p.role==UserRole.ADMIN_CAT)"Роль: sysadmin-кот" else "Роль: заказчик";if(!filled)b.etName.setText(p.displayName);b.adminSection.visibility=if(p.role==UserRole.ADMIN_CAT)View.VISIBLE else View.GONE;b.tvClientHint.visibility=if(p.role==UserRole.CLIENT)View.VISIBLE else View.GONE}}}
        lifecycleScope.launch{vm.listing.collect{l->if(l!=null&&!filled){filled=true;b.etTitle.setText(l.roleTitle);b.etExperience.setText(l.experienceYears.toString());b.etPrice.setText(l.priceByService.toString());b.etBio.setText(l.shortBio);b.cbVpn.isChecked="VPN" in l.categories;b.cbMikrotik.isChecked="MikroTik" in l.categories;b.cbCisco.isChecked="Cisco" in l.categories;b.cbLinux.isChecked="Linux" in l.categories;b.cbSecurity.isChecked="Security" in l.categories;b.tvPublicationState.text=if(l.published)"Карточка сейчас опубликована" else "Сейчас это черновик";b.etName.setSelection(0);b.root.requestFocus()}}}
        lifecycleScope.launch{vm.message.collect{m->if(m!=null){Toast.makeText(this@ProfileActivity,m,Toast.LENGTH_LONG).show();vm.consumeMessage()}}}
    }
    private fun categories()=buildList{if(b.cbVpn.isChecked)add("VPN");if(b.cbMikrotik.isChecked)add("MikroTik");if(b.cbCisco.isChecked)add("Cisco");if(b.cbLinux.isChecked)add("Linux");if(b.cbSecurity.isChecked)add("Security")}
    private fun saveListing(publish:Boolean){val exp=b.etExperience.text.toString().toLongOrNull();val price=b.etPrice.text.toString().toLongOrNull();val err=Validators.listingError(b.etTitle.text.toString(),categories(),exp,price,b.etBio.text.toString());b.tvListingError.text=err.orEmpty();b.tvListingError.visibility=if(err==null)View.GONE else View.VISIBLE;if(err==null)vm.saveListing(b.etTitle.text.toString(),categories(),exp!!,price!!,b.etBio.text.toString(),publish)}
    private fun showError(e:String?){b.tvNameError.text=e.orEmpty();b.tvNameError.visibility=if(e==null)View.GONE else View.VISIBLE}
}
