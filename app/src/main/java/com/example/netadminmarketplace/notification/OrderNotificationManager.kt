package com.example.netadminmarketplace.notification
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.netadminmarketplace.R
import com.example.netadminmarketplace.model.Order
class OrderNotificationManager(private val context:Context){private val channelId="orders";fun showOrderCreated(order:Order){createChannel();if(ActivityCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)return;val n=NotificationCompat.Builder(context,channelId).setSmallIcon(R.drawable.ic_launcher).setContentTitle("SysAdMeow: заказ ${order.id} оформлен").setContentText("Оплата успешна · ${order.total} BYN").setPriority(NotificationCompat.PRIORITY_DEFAULT).build();NotificationManagerCompat.from(context).notify(order.id.hashCode(),n)};private fun createChannel(){context.getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(channelId,"Заказы SysAdMeow",NotificationManager.IMPORTANCE_DEFAULT))}}
