package com.iab.GJobs.services;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iab.GJobs.DetailActivity;
import com.iab.GJobs.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NotificationChanel = "DefaultNotificationChannelId";
    public static final int NotificationID = 1011;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String action = "cat_list_data";
        String id = remoteMessage.getData().get("id");
        String cat_id = remoteMessage.getData().get("cat_id");
        createNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(),
                action,
                id,
                cat_id);
    }

    public void createNotification(String title, String bodyMessage, String action, String id, String cat_id){
// Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, DetailActivity.class);
        resultIntent.putExtra("action", action);
        resultIntent.putExtra("id", id);
        resultIntent.putExtra("cat_id", cat_id);
        resultIntent.putExtra("Job_title", title);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification Build
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChanel)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(bodyMessage)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.background))
                .setColorized(true)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NotificationID, builder.build());


    }
}
