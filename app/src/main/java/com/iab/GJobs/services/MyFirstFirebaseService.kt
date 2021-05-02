package com.iab.GJobs.services

import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.iab.GJobs.DetailActivity
import com.iab.GJobs.R

class MyFirstFirebaseService : FirebaseMessagingService() {
    val NotificationChanel = "DefaultNotificationChannelId"
    val NotificationID = 1011

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val action = "cat_list_data"
        val id = remoteMessage.data["id"]
        val cat_id = remoteMessage.data["cat_id"]
        createNotification(
            remoteMessage.notification!!.title,
            remoteMessage.notification!!.body,
            action,
            id,
            cat_id
        )
    }

    fun createNotification(
        title: String?,
        bodyMessage: String?,
        action: String?,
        id: String?,
        cat_id: String?
    ) {
// Create an Intent for the activity you want to start
        val resultIntent = Intent(this, DetailActivity::class.java)
        resultIntent.putExtra("action", action)
        resultIntent.putExtra("id", id)
        resultIntent.putExtra("cat_id", cat_id)
        resultIntent.putExtra("Job_title", title)
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        // Get the PendingIntent containing the entire back stack
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //        Notification Build

        val soundUri:Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(
            this,
            NotificationChanel
        )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(bodyMessage)
                .setAutoCancel(true)
                .setColor(resources.getColor(R.color.background))
                .setColorized(true)
                .setSound(soundUri)
                .setContentIntent(resultPendingIntent)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(NotificationID, builder.build())
    }

}