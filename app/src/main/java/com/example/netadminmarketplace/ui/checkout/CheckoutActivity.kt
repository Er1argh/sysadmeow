package com.example.netadminmarketplace.ui.checkout
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.netadminmarketplace.data.CartRepository
import com.example.netadminmarketplace.data.OrderRepository
import com.example.netadminmarketplace.databinding.ActivityCheckoutBinding
import com.example.netadminmarketplace.notification.OrderNotificationManager
import com.example.netadminmarketplace.ui.main.MainActivity
import com.example.netadminmarketplace.ui.payment.PaymentActivity
class CheckoutActivity:AppCompatActivity(){private lateinit var b:ActivityCheckoutBinding
    private val paymentLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){r->if(r.resultCode==RESULT_OK)onPaymentSuccess()else{b.tvAfterPayment.text="Оплата не завершена. Корзина сохранена — можно попробовать ещё раз.";b.tvAfterPayment.visibility=View.VISIBLE;b.btnPay.isEnabled=true}}
    override fun onCreate(s:Bundle?){super.onCreate(s);b=ActivityCheckoutBinding.inflate(layoutInflater);setContentView(b.root);b.tvSummary.text="К оплате: ${CartRepository.total()} BYN";b.btnBack.setOnClickListener{finish()};b.btnPay.setOnClickListener{if(CartRepository.items.value.isNotEmpty()){b.btnPay.isEnabled=false;paymentLauncher.launch(Intent(this,PaymentActivity::class.java).putExtra("amount",CartRepository.total()))}};b.btnDone.setOnClickListener{startActivity(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));finish()}}
    private fun onPaymentSuccess(){b.btnPay.visibility=View.GONE;b.progress.visibility=View.VISIBLE;b.tvAfterPayment.visibility=View.VISIBLE;b.tvAfterPayment.text="Платёж подтверждён. Сохраняем заказ…";OrderRepository.createPaidOrder(CartRepository.items.value){r->runOnUiThread{b.progress.visibility=View.GONE;r.onSuccess{o->OrderNotificationManager(this).showOrderCreated(o);CartRepository.clear();b.tvSummary.text="Заказ ${o.id} успешно оформлен";b.tvAfterPayment.text="Готово! Оплата успешна, заказ сохранён. Sysadmin-кот увидит задачу.";b.resultCard.visibility=View.VISIBLE;b.btnDone.visibility=View.VISIBLE}.onFailure{b.tvAfterPayment.text="Оплата прошла, но заказ не записался: ${it.localizedMessage}. Не закрывайте экран и обратитесь к разработчику."}}}}
}
