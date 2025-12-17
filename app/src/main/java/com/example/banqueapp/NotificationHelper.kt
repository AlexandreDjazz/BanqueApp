package com.example.banqueapp
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
object NotificationHelper {

    private const val CHANNEL_ID = "banqueapp_channel"
    private const val CHANNEL_NAME = "Notifications BanqueApp"
    private const val CHANNEL_DESCRIPTION = "Alertes transactions et comptes"

    fun createChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            importance
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun requestNotificationPermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (context is ComponentActivity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
            return false
        }
        return true
    }

    fun send(context: Context, title: String, message: String) {
        send(context, title, message, 0)
    }

    fun send(context: Context, title: String, message: String, notificationId: Int = 0) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            internalSend(context, title, message, notificationId)
            return
        }

        if(!requestNotificationPermission(context)){
            return
        }

        internalSend(context, title, message, notificationId)
    }

    private fun internalSend(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        createChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(
            notificationId.ifZero(notificationId),
            builder.build()
        )
    }

    private fun Int.ifZero(id: Int): Int = if (this == 0) System.currentTimeMillis().toInt() else id
}
