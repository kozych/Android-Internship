package com.example.todokotlin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat


class NotifyService: Service() {
    val NOTIFICATION_CHANNEL_ID = "10001"
    private val default_notification_channel_id = "default"

    override fun onBind(intent: Intent?): IBinder? {
        val notificationIntent: Intent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.putExtra("fromNotification", true)
//        var intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        intentFlag = intentFlag | Intent.FLAG_ACTIVITY_SINGLE_TOP
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val mNotificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, default_notification_channel_id)
        mBuilder.setContentTitle("Test Notification")
            .setContentIntent(pendingIntent)
            .setContentText("Notification Listener Service Example")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            assert(mNotificationManager != null)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
        throw UnsupportedOperationException("Not yet implemented")
    }
}