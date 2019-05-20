package com.example.myapplication;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationActionReceiver extends BroadcastReceiver {

    String TB4 = "TB4";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference(TB4);
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.getAction().equalsIgnoreCase("CONFIRM")) {
            String type = intent.getExtras().getString("action_type");
            if (type.equals("1")) {
                databaseReference.setValue("1");
            } else {
                databaseReference.setValue("0");
            }
            notificationManager.cancel(11111);

        } else if (intent.getAction().equalsIgnoreCase("CANCEL")) {
            notificationManager.cancel(11111);

        }
    }
}
