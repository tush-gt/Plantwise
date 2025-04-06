package com.example.plantwise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val plantName = intent.getStringExtra("plantName")
        val notification = NotificationCompat.Builder(context, "PlantReminder")
            .setContentTitle("Time to Water 🌿")
            .setContentText("Don't forget to water your $plantName!")
            .setSmallIcon(R.drawable.ic_water_drop)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU ||
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1001, notification)
        }
    }

}
