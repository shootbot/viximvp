package com.shootbot.viximvp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shootbot.viximvp.activities.IncomingInvitationActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shootbot.viximvp.CallNotificationService
import com.shootbot.viximvp.utilities.Constants

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val type = remoteMessage.data[Constants.REMOTE_MSG_TYPE]


        if (Constants.REMOTE_MSG_INVITATION != null && Constants.REMOTE_MSG_INVITATION == type) {
            val intent = Intent(applicationContext, CallNotificationService::class.java)
            intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, remoteMessage.data[Constants.REMOTE_MSG_MEETING_TYPE])
            intent.putExtra(Constants.KEY_FIRST_NAME, remoteMessage.data[Constants.KEY_FIRST_NAME])
            intent.putExtra(Constants.KEY_LAST_NAME, remoteMessage.data[Constants.KEY_LAST_NAME])
            intent.putExtra(Constants.KEY_EMAIL, remoteMessage.data[Constants.KEY_EMAIL])
            intent.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN, remoteMessage.data[Constants.REMOTE_MSG_INVITER_TOKEN])
            intent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, remoteMessage.data[Constants.REMOTE_MSG_MEETING_ROOM])
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startService(intent)
//            startActivity(intent)
        } else if (Constants.REMOTE_MSG_INVITATION_RESPONSE != null && Constants.REMOTE_MSG_INVITATION_RESPONSE == type) {
            val intent = Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            intent.putExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE, remoteMessage.data[Constants.REMOTE_MSG_INVITATION_RESPONSE])
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

            stopService(Intent(applicationContext, CallNotificationService::class.java))
            val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            sendBroadcast(it)
        }
    }

}