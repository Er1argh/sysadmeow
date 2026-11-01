package com.example.netadminmarketplace.ui.payment
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.begateway.mobilepayments.models.network.CheckoutWithToken
import com.begateway.mobilepayments.models.network.request.Checkout
import com.begateway.mobilepayments.models.network.request.Order as PaymentOrder
import com.begateway.mobilepayments.models.network.request.SaveCardPolicy
import com.begateway.mobilepayments.models.network.request.Settings
import com.begateway.mobilepayments.models.network.request.TokenCheckoutData
import com.begateway.mobilepayments.models.network.request.TransactionType
import com.begateway.mobilepayments.models.network.response.BeGatewayResponse
import com.begateway.mobilepayments.models.network.response.CheckoutWithTokenData
import com.begateway.mobilepayments.sdk.OnResultListener
import com.begateway.mobilepayments.sdk.PaymentSdk
import com.begateway.mobilepayments.sdk.PaymentSdkBuilder
import com.example.netadminmarketplace.R
import com.example.netadminmarketplace.databinding.ActivityPaymentBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
class PaymentActivity:AppCompatActivity(),OnResultListener{private lateinit var b:ActivityPaymentBinding;private lateinit var sdk:PaymentSdk;private var amount=0L;private var success=false
    override fun onCreate(s:Bundle?){super.onCreate(s);b=ActivityPaymentBinding.inflate(layoutInflater);setContentView(b.root);amount=intent.getLongExtra("amount",0);b.tvAmount.text="Сумма: $amount BYN";setup();b.btnPay.setOnClickListener{lifecycleScope.launch{try{b.btnPay.isEnabled=false;b.progress.visibility=View.VISIBLE;startPayment(amount*100)}catch(e:Exception){showResult(false,"ERROR",e.localizedMessage?:"Ошибка SDK")}finally{b.progress.visibility=View.GONE;if(!success)b.btnPay.isEnabled=true}}};b.btnClose.setOnClickListener{setResult(if(success)Activity.RESULT_OK else Activity.RESULT_CANCELED);finish()}}
    private fun setup(){sdk=PaymentSdkBuilder().apply{setDebugMode(true);setPublicKey(PUBLIC_STORE_KEY);setCardNumberFieldVisibility(true);setCardCVCFieldVisibility(true);setCardDateFieldVisibility(true);setCardHolderFieldVisibility(true);setNFCScanVisibility(true);setEndpoint(CHECKOUT_ENDPOINT)}.build(this).apply{addCallBackListener(this@PaymentActivity)}}
    private suspend fun startPayment(cents:Long){sdk.getPaymentToken(TokenCheckoutData(Checkout(test=true,transactionType=TransactionType.PAYMENT,order=PaymentOrder(amount=cents,currency="BYN",description="SysAdMeow network service",trackingId="sysadmeow_${FirebaseAuth.getInstance().uid?:"guest"}_${System.currentTimeMillis()}"),settings=Settings(saveCardPolicy=SaveCardPolicy(false)))))}
    override fun onTokenReady(token:String){runOnUiThread{sdk.checkoutWithTokenData=CheckoutWithTokenData(CheckoutWithToken(token=token));startActivity(PaymentSdk.getCardFormIntent(this))}}
    override fun onPaymentFinished(response:BeGatewayResponse,cardToken:String?){runOnUiThread{success=response.status.name=="SUCCESS";showResult(success,response.status.name,response.message.orEmpty())}}
    private fun showResult(ok:Boolean,status:String,message:String){setResult(if(ok) Activity.RESULT_OK else Activity.RESULT_CANCELED);b.resultCard.visibility=View.VISIBLE;b.resultCard.setBackgroundResource(if(ok)R.drawable.bg_success else R.drawable.bg_failure);b.tvResultTitle.text=if(ok)"Оплата успешна" else "Оплата не выполнена";b.tvResultStatus.text="Статус: $status" + if (message.isNotBlank()) "\n$message" else "";b.tvResultHint.text=if(ok)"Нажмите «Продолжить»: SysAdMeow создаст заказ и очистит корзину." else "Деньги не подтверждены. Можно закрыть окно и повторить оплату.";b.btnClose.text=if(ok)"Продолжить" else "Вернуться";b.btnPay.visibility=if(ok)View.GONE else View.VISIBLE}
    companion object{const val PUBLIC_STORE_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvextn45qf3NiNzqBYXMvcaSFlgoYE/LDuDDHtNNM4iWJP7BvjBkPcZu9zAfo5IiMxl660r+1E4PYWwr0iKSQ8+7C/WcSYwP8WlQVZH+2KtPmJgkPcBovz3/aZrQpj6krcKLklihg3Vs++TtXAbpCCbhIq0DJ3T+khttBqTGD+2x2vOC68xPgMwvnwQinfhaHEQNbtEcWWXPw9LYuOTuCwKlqijAEds4LgKSisubqrkRw/HbAKVfa659l5DJ8QuXctjp3Ic+7P2TC+d+rcfylxKw9c61ykHS1ggI/N+/KmEDVJv1wHvdy7dnT0D/PhArnCB37ZDAYErv/NMADz2/LuQIDAQAB";const val CHECKOUT_ENDPOINT="https://checkout.bepaid.by/ctp/api/"}
}
