package com.example.netadminmarketplace.ui.main
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.netadminmarketplace.databinding.ActivityMainBinding
import com.example.netadminmarketplace.ui.common.BottomNav
import com.example.netadminmarketplace.ui.detail.SpecialistDetailActivity
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding; private val viewModel: MainViewModel by viewModels()
    private val categories = CategoryAdapter { viewModel.selectCategory(it) }
    private var openingDetails = false
    private val specialists = SpecialistAdapter { id ->
        if (!openingDetails) { openingDetails = true; startActivity(Intent(this, SpecialistDetailActivity::class.java).putExtra("adminId", id)) }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); binding=ActivityMainBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.rvCategories.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false); binding.rvCategories.adapter=categories
        binding.rvSpecialists.layoutManager=LinearLayoutManager(this); binding.rvSpecialists.adapter=specialists
        BottomNav.bind(this,binding.bottomNav.navHome,binding.bottomNav.navCart,binding.bottomNav.navProfile,BottomNav.Current.HOME)
        lifecycleScope.launch { viewModel.admins.collect { specialists.submit(it); binding.tvEmpty.visibility=if(it.isEmpty()) View.VISIBLE else View.GONE } }
        lifecycleScope.launch { viewModel.selectedCategory.collect { categories.setSelected(it); binding.tvFilterHint.text="Фильтр: $it" } }
        lifecycleScope.launch { viewModel.error.collect { binding.tvError.text=it.orEmpty(); binding.tvError.visibility=if(it==null) View.GONE else View.VISIBLE } }
        requestNotifications()
    }
    override fun onResume() { super.onResume(); openingDetails=false }
    private fun requestNotifications() { if(Build.VERSION.SDK_INT>=33 && ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.POST_NOTIFICATIONS),200) }
}
