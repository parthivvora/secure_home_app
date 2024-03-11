package com.example.securehome

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@Suppress("DEPRECATION")
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessagingService : FirebaseMessagingService() {
    private var mNotificationManager: NotificationManager? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val channelId = "932004"
        // playing audio and vibration
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.isLooping = false
        }

        // vibration
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(100, 300, 300, 300)
        v.vibrate(pattern, -1)

        val builder = NotificationCompat.Builder(this@FirebaseMessagingService, channelId)
        builder.setSmallIcon(R.drawable.app_icon_logo)

        val resultIntent = Intent(this@FirebaseMessagingService, NotificationActivity::class.java)
        resultIntent.putExtra("title", remoteMessage.notification?.title)
        resultIntent.putExtra("body", remoteMessage.notification?.body)

        val pendingIntent =
            PendingIntent.getActivity(
                this@FirebaseMessagingService,
                1,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        builder.setContentTitle(remoteMessage.notification!!.title)
        builder.setContentText(remoteMessage.notification!!.body)
        builder.setContentIntent(pendingIntent)
        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(
                remoteMessage.notification!!.body
            )
        )
        builder.setAutoCancel(true)
        builder.setPriority(Notification.PRIORITY_MAX)
        mNotificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Vora Parthiv",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager!!.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }


// notificationId is a unique int for each notification that you must define
        mNotificationManager!!.notify(100, builder.build())
    }
}
