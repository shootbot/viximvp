package com.shootbot.viximvp.network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.shootbot.viximvp.CallNotificationService;
import com.shootbot.viximvp.activities.MainActivity;
import com.shootbot.viximvp.utilities.*;

import static com.shootbot.viximvp.utilities.Constants.KEY_EMAIL;
import static com.shootbot.viximvp.utilities.Constants.KEY_FIRST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_LAST_NAME;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_RESPONSE;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITER_TOKEN;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_MEETING_ROOM;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_MEETING_TYPE;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_TYPE;

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // String notificationTitle = "MyApp";
        // String notificationText = "Test notification";
        //
        // // Attempt to extract the "message" property from the payload: {"message":"Hello World!"}
        // if (intent.getStringExtra("message") != null) {
        //     notificationText = intent.getStringExtra("message");
        // }

        ///////////////////////////////////////////////////
        // Prepare a notification with vibration, sound and lights
        // NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
        //         .setAutoCancel(true)
        //         .setSmallIcon(android.R.drawable.ic_dialog_info)
        //         .setContentTitle(notificationTitle)
        //         .setContentText(notificationText)
        //         .setLights(Color.RED, 1000, 1000)
        //         .setVibrate(new long[]{0, 400, 250, 400})
        //         .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        //         .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        //
        // // Automatically configure a Notification Channel for devices running Android O+
        // Pushy.setNotificationChannel(builder, context);
        //
        // // Get an instance of the NotificationManager service
        // NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //
        // // Build the notification and display it
        // notificationManager.notify(1, builder.build());
        ////////////////////////////////////////////////

        Log.d("PushReceiver", "onReceive intent first name: " + intent.getStringExtra(Constants.KEY_FIRST_NAME));
        Log.d("PushReceiver", "onReceive intent inviter token: " + intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        Log.d("PushReceiver", "onReceive intent meeting room: " + intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
        Log.d("PushReceiver", "onReceive intent msg type: " + intent.getStringExtra(REMOTE_MSG_TYPE));

        String type = intent.getStringExtra(REMOTE_MSG_TYPE);

        if (REMOTE_MSG_INVITATION.equals(type)) {
            Intent i = new Intent(context, CallNotificationService.class);
            i.putExtra(REMOTE_MSG_MEETING_TYPE, intent.getStringExtra(REMOTE_MSG_MEETING_TYPE));
            i.putExtra(KEY_FIRST_NAME, intent.getStringExtra(KEY_FIRST_NAME));
            i.putExtra(KEY_LAST_NAME, intent.getStringExtra(KEY_LAST_NAME));
            i.putExtra(KEY_EMAIL, intent.getStringExtra(KEY_EMAIL));
            i.putExtra(REMOTE_MSG_INVITER_TOKEN, intent.getStringExtra(REMOTE_MSG_INVITER_TOKEN));
            i.putExtra(REMOTE_MSG_MEETING_ROOM, intent.getStringExtra(REMOTE_MSG_MEETING_ROOM));

            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // startActivity(intent)

            context.startService(i);
        } else if (REMOTE_MSG_INVITATION_RESPONSE.equals(type)) {
            Intent i = new Intent(REMOTE_MSG_INVITATION_RESPONSE);
            i.putExtra(REMOTE_MSG_INVITATION_RESPONSE, intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE));
            LocalBroadcastManager.getInstance(context).sendBroadcast(i);

            context.stopService(new Intent(context, CallNotificationService.class));
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    }
}