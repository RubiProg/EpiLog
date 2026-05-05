package com.epilog.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.epilog.app.R

class MedicacionReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "epilog_medicacion"
        const val EXTRA_NOMBRE = "nombre"
        const val EXTRA_DOSIS = "dosis"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val nombre = intent.getStringExtra(EXTRA_NOMBRE) ?: "Medicamento"
        val dosis = intent.getStringExtra(EXTRA_DOSIS) ?: ""

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorio de medicación",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle("Hora de tomar $nombre")
            .setContentText(dosis)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
