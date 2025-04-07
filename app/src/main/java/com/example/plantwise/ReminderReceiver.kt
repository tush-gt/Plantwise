package com.example.plantwise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val plantName = intent.getStringExtra("plantName") ?: "your plant"
        val plantId = intent.getStringExtra("plantId") // 🌟 get plantId

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "watering_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Watering Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to water your plant 🌱"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("Time to water 💧")
            .setContentText("Don't forget to water $plantName!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // 🌟 use plantId's hashCode to uniquely identify the notification
        val notificationId = plantId?.hashCode() ?: plantName.hashCode()
        notificationManager.notify(notificationId, notification)
    }
}
