package com.mlfikt.notemaster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Random;

public class Utility {


    static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    //create collection for every user
    static CollectionReference getCollectionReferenceForNotes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes")
                .document(currentUser.getUid()).collection("my_notes");
    }

    static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("MM/dd/yyyy").format(timestamp.toDate());
    }

    static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showNotification(Context context, String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "com.mlfikt.notemaste");

        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        //style
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(title);
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.setSummaryText(title);

        builder.setStyle(bigTextStyle);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanelId = "com.mlfikt.notemaste.id";
            NotificationChannel channel = new NotificationChannel(chanelId, "MLFIKTDEV Chanel",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(chanelId);
        }

        manager.notify(new Random().nextInt(), builder.build());
    }
}
