package com.govt.spm.notification;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.govt.spm.R;
import com.govt.spm.SplashScreen;

public class NotificationFirebaseService extends FirebaseMessagingService {
    final static String TAG = "SPA_ERROR";
    private static final String CHANNEL_ID = "102";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
//        Log.i(TAG, "onMessageReceived: "+message.getNotification().getTitle());
//        Log.i(TAG, "onMessageReceived: "+message.getNotification().getBody());
        showNotification(message.getNotification().getTitle(),message.getNotification().getBody());
    }
    public void showNotification(String title,String message){

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, SplashScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());


    }
}
