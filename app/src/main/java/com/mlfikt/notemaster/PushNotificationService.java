package com.mlfikt.notemaster;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class PushNotificationService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        //Show notification when we receive it
        String title = message.getNotification().getTitle();
        String content = message.getNotification().getBody();
        String data = new Gson().toJson(message.getData());

        //Create notification to show
        Utility.showNotification(this, title, content);

    }
}
