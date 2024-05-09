package com.demo.sharingapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.demo.sharingapp.domain.chat.chatroom.ChatRoomActivity
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.NOTIFY_STATE
import com.demo.sharingapp.utils.Constants.PUSH_MESSAGE
import com.google.android.material.internal.ManufacturerUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notifyState = SharedPreferencesData.getBooleanData(this, NOTIFY_STATE)

        if(notifyState){
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

            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(PUSH_MESSAGE, "gkdl")


            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, 0
            )


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
                .setContentIntent(pendingIntent)  // 클릭 액션 지정
                .setAutoCancel(true)  // 알림을 클릭하면 자동으로 알림이 사라지도록 설정

            notificationManager.notify(0, notificationBuilder.build())

        }



    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }



}