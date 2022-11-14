package com.example.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        val DATA_KEY = "data_key"
        val TAG = "com.example.notify"
        val MESSAGE_CHANNEL = "message_chanel"
        val MESSAGE_ID: Int = 1001
    }

    override fun doWork(): Result {
        Log.i(TAG, "doWork: working")
        val data: Data = Data.Builder()
            .putString(DATA_KEY, "Hello")
            .build()
        val response = inputData.getString(DATA_KEY)
        Log.i(TAG, "Received $response")
        displayNotification(response!!, data.getString(DATA_KEY)!!)
        return Result.success(data)
    }

    private fun displayNotification(title: String, message: String) {
        val notificationManager: NotificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel = NotificationChannel(MESSAGE_CHANNEL,
                "Task Notification",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext, MESSAGE_CHANNEL)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.star_on)
        notificationManager.notify(MESSAGE_ID, notification.build())
    }

}