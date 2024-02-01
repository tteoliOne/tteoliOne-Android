package com.demo.sharingapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.android.material.internal.ManufacturerUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val name = "채팅 알림"
        val descriptionText = "채팅 알림 입니다." //채널에 대한 설명
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance)
        mChannel.description = descriptionText
        mChannel.enableLights(true)
        mChannel.lightColor = Color.RED


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        val title = message.notification?.title ?: ""
        val body = message.notification?.body ?: ""


        val parts = body.split(" : ")
        val boldText = parts.firstOrNull() ?: ""  // " : " 이 없을 경우 기본값으로 처리

// 굵게 표시할 부분을 만듭니다.
        val spannableString = SpannableString(body)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, boldText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val notificationBuilder = NotificationCompat.Builder(applicationContext,getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.app_logo)
            .setColor(Color.parseColor("#588F11"))

            .setContentTitle(title)
            .setContentText(spannableString)



        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


}