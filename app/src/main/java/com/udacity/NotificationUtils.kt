package com.udacity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.ApplicationConstants.INTENT_DOWNLOAD_STATUS
import com.udacity.ApplicationConstants.INTENT_REPOSITORY_NAME

private val NOTIFICATION_ID = 123456

fun NotificationManager.
        sendNotification(messageBody: String, applicationContext: Context, fileName: String, downloadStatus : String) {
    // Create the content intent for the notification, which launches
    // this activity
    // TODO: Step 1.11 create intent


    notify(NOTIFICATION_ID, generateNotification(messageBody,applicationContext,fileName,downloadStatus))
}

private fun generateNotification(messageBody: String, applicationContext: Context, fileName: String, downloadStatus : String): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val notificationChannel = NotificationChannel(
            applicationContext.getString(R.string.notification_channel_id),
            applicationContext.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        //Disable the sound in the notification tray
        //notificationChannel.setSound(null, null);
        // Adds NotificationChannel to system. Attempting to create an
        // existing notification channel with its original values performs
        // no operation, so it's safe to perform the below sequence.
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

        val contentIntent = Intent(applicationContext,DetailActivity::class.java)

        contentIntent.putExtra(INTENT_DOWNLOAD_STATUS, downloadStatus)
        contentIntent.putExtra(INTENT_REPOSITORY_NAME, fileName)

        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return  NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentText(messageBody)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                applicationContext.getString(R.string.notification_button),
                contentPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }