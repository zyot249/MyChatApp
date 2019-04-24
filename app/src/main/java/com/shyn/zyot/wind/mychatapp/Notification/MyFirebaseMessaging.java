package com.shyn.zyot.wind.mychatapp.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shyn.zyot.wind.mychatapp.MessageActivity;
import com.shyn.zyot.wind.mychatapp.Model.Room;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    String roomID;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sender = remoteMessage.getData().get("userID");
        String receiver = remoteMessage.getData().get("receiverID");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        if (firebaseUser != null && sender.equals(firebaseUser.getUid())){
////            if (!currentUser.equals(receiver)) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    sendOreoNotification(remoteMessage);
//                } else {
//                    sendNotification(remoteMessage);
//                }
////            }
//        }
    }

    private void getRoomID(String userID){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRoom = FirebaseDatabase.getInstance().getReference("UserRooms").child(firebaseUser.getUid()).child(userID);
        userRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                roomID = room.getRoomID();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNotification(RemoteMessage remoteMessage) {
        String receiver = remoteMessage.getData().get("receiverID");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        getRoomID(receiver);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int requestCode = Integer.parseInt(receiver.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("receiverID", receiver);
        intent.putExtra("roomID", roomID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int key = 0;
        if (requestCode > 0) {
            key = requestCode;
        }

        noti.notify(key, builder.build());
    }


    public void sendOreoNotification(RemoteMessage remoteMessage) {
        String receiver = remoteMessage.getData().get("receiverID");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        getRoomID(receiver);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int requestCode = Integer.parseInt(receiver.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("receiverID", receiver);
        bundle.putString("roomID", roomID);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon);

        int key = 0;
        if (requestCode > 0) {
            key = requestCode;
        }

        oreoNotification.getManager().notify(key, builder.build());
    }

}
