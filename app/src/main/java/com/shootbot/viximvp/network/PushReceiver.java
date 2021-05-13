package com.shootbot.viximvp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.shootbot.viximvp.utilities.Constants;
import com.shootbot.viximvp.utilities.PreferenceManager;

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
        Log.d("PushReceiver", "onReceive intent message: " + intent.getStringExtra("message"));
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