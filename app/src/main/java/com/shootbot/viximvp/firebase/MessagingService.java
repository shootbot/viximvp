package com.shootbot.viximvp.firebase;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shootbot.viximvp.activities.IncomingInvitationActivity;

import static com.shootbot.viximvp.utilities.Constants.*;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get(REMOTE_MSG_TYPE);

        if (REMOTE_MSG_INVITATION.equals(type)) {
            Intent intent = new Intent(getApplicationContext(), IncomingInvitationActivity.class);

            intent.putExtra(REMOTE_MSG_MEETING_TYPE, remoteMessage.getData().get(REMOTE_MSG_MEETING_TYPE));
            intent.putExtra(KEY_FIRST_NAME, remoteMessage.getData().get(KEY_FIRST_NAME));
            intent.putExtra(KEY_LAST_NAME, remoteMessage.getData().get(KEY_LAST_NAME));
            intent.putExtra(KEY_EMAIL, remoteMessage.getData().get(KEY_EMAIL));
            intent.putExtra(REMOTE_MSG_INVITER_TOKEN, remoteMessage.getData().get(REMOTE_MSG_INVITER_TOKEN));
            intent.putExtra(REMOTE_MSG_MEETING_ROOM, remoteMessage.getData().get(REMOTE_MSG_MEETING_ROOM));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        } else if (type.equals(REMOTE_MSG_INVITATION_RESPONSE)) {
            Intent intent = new Intent(REMOTE_MSG_INVITATION_RESPONSE);
            intent.putExtra(REMOTE_MSG_INVITATION_RESPONSE, remoteMessage.getData().get(REMOTE_MSG_INVITATION_RESPONSE));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }
}
