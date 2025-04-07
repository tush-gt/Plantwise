package com.example.plantwise


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            if (uid != null) {
                val firestore = FirebaseFirestore.getInstance()

                firestore.collection("user_plants")
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for (doc in snapshot.documents) {
                            val name = doc.getString("name") ?: continue
                            val hour = doc.getLong("hour")?.toInt() ?: continue
                            val minute = doc.getLong("minute")?.toInt() ?: continue

                            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                            val reminderIntent = Intent(context, ReminderReceiver::class.java).apply {
                                putExtra("plantName", name)
                            }

                            val pendingIntent = android.app.PendingIntent.getBroadcast(
                                context,
                                name.hashCode(),
                                reminderIntent,
                                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                            )

                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                                set(Calendar.SECOND, 0)
                                if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
                            }

                            alarmManager.setRepeating(
                                android.app.AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                android.app.AlarmManager.INTERVAL_DAY,
                                pendingIntent
                            )
                        }
                    }
                    .addOnFailureListener {
                        Log.e("BootReceiver", "Failed to restore reminders 🥺", it)
                    }
            } else {
                Log.d("BootReceiver", "No user logged in at boot time 🚫")
            }
        }
    }
}
